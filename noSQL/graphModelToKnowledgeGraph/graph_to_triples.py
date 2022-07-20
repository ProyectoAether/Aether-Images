import hashlib
import io
import os

import pydotplus
import typer
import xmltodict as x2d
from rdflib import URIRef, Graph, Namespace, RDF, XSD, Literal, RDFS

from rdf_to_dot import rdf_to_dot


def parse2rdf(
        filepath: str = typer.Option(..., help="The name of the model file"),
):
    def read_xml(path: str) -> dict:
        with open(path, 'r') as file:
            file_xml = file.read()
            parser_xml = x2d.parse(file_xml, item_depth=0)
            return parser_xml

    def visualize(graph: Graph):
        stream = io.StringIO()
        rdf_to_dot(graph, stream)
        dg = pydotplus.graph_from_dot_data(stream.getvalue())
        dg.write_pdf("graph.pdf")

    def parse_fields(name_uri: URIRef, fields: dict):
        if "fields" in fields:
            fields = fields["fields"]
            if type(fields) is dict:
                name_field = fields["@name"]
                field_type = fields["dataType"]

                id_field = hashlib.sha224(fields["@xmi:id"].encode('utf-8')).hexdigest()
                uri_field = URIRef(databases + id_field)
                rdf_graph.add((uri_field, RDF.type, databases.NoSQLFieldGraph))
                rdf_graph.add((uri_field, bigowl.hasDataType, bigowl[field_type["@xsi:type"]]))
                rdf_graph.add(
                    (uri_field, databases.fieldhasName, Literal(name_field, datatype=XSD['string'])))
                rdf_graph.add(
                    (uri_field, RDFS.label, Literal(f"Field {name_field}", datatype=XSD['string'])))
                rdf_graph.add(
                    (name_uri, databases.hasElementField, uri_field))
            else:
                for field in fields:
                    if type(field) is dict:
                        name_field = field["@name"]
                        field_type = field["dataType"]
                        id_field = hashlib.sha224(field["@xmi:id"].encode('utf-8')).hexdigest()
                        uri_field = URIRef(databases + id_field)
                        rdf_graph.add((uri_field, RDF.type, databases.NoSQLFieldGraph))
                        rdf_graph.add((uri_field, bigowl.hasDataType, bigowl[field_type["@xsi:type"]]))
                        rdf_graph.add(
                            (uri_field, databases.fieldhasName, Literal(name_field, datatype=XSD['string'])))
                        rdf_graph.add(
                            (uri_field, RDFS.label, Literal(f"Field {name_field}", datatype=XSD['string'])))
                        rdf_graph.add(
                            (name_uri, databases.hasElementField, uri_field))

    def parse_elements(classifiers_list: dict):
        for classifier in classifiers_list:
            type_classifier = classifier["@xsi:type"]
            name_classifier = classifier["@name"]
            if type_classifier == "Node":
                id_node = hashlib.sha224(classifier["@xmi:id"].encode('utf-8')).hexdigest()
                uri_node = URIRef(databases + id_node)
                rdf_graph.add((uri_database, databases.hasElements, uri_node))
                rdf_graph.add((uri_node, RDF.type, databases.Node))
                rdf_graph.add((uri_node, databases.elementHasName, Literal(name_classifier, datatype=XSD['string'])))
                rdf_graph.add(
                    (uri_node, RDFS.label, Literal(f"Node {name_classifier}", datatype=XSD['string'])))
                print("******", type_classifier, ":", name_classifier, "******", )
                parse_fields(uri_node, classifier)
            elif type_classifier == "Relationship":
                print("******", type_classifier, ":", name_classifier, "******", )

                id_relationship = hashlib.sha224(classifier["@xmi:id"].encode('utf-8')).hexdigest()
                uri_relationship = URIRef(databases + id_relationship)
                rdf_graph.add((uri_database, databases.hasElements, uri_relationship))
                rdf_graph.add((uri_relationship, RDF.type, databases.Relationship))
                rdf_graph.add(
                    (uri_relationship, databases.elementHasName, Literal(name_classifier, datatype=XSD['string'])))

                rdf_graph.add(
                    (uri_relationship, RDFS.label, Literal(f"Relationship {name_classifier}", datatype=XSD['string'])))

                parse_fields(uri_relationship, classifier)

                id_node_from = hashlib.sha224(classifier["@from"].encode('utf-8')).hexdigest()
                uri_node_from = URIRef(databases + id_node_from)
                rdf_graph.add(
                    (uri_relationship, databases.hasRelationshipFrom, uri_node_from))

                id_node_to = hashlib.sha224(classifier["@to"].encode('utf-8')).hexdigest()
                uri_node_to = URIRef(databases + id_node_to)
                rdf_graph.add(
                    (uri_relationship, databases.hasRelationshipto, uri_node_to))

            else:
                print(f"TIPO NO CONTEMPLADO: {type_classifier}")

    def parse_children(uri_rol, children: dict):
        id_children = hashlib.sha224(children["@xmi:id"].encode('utf-8')).hexdigest()
        uri_children = URIRef(security + id_children)
        rdf_graph.add((uri_children, RDF.type, security.Role))
        rdf_graph.add((uri_database, security.DatabaseHasRole, uri_children))
        rdf_graph.add((uri_rol, security.HasSubRole, uri_children))
        rdf_graph.add(
            (uri_children, RDFS.label, Literal(f"Role {children['@name']}", datatype=XSD['string'])))
        rdf_graph.add(
            (uri_children, security.roleName, Literal(children['@name'], datatype=XSD['string'])))
        if '@rules' in children:
            for rule in children['@rules'].split(" "):
                id_rule = hashlib.sha224(rule.encode('utf-8')).hexdigest()
                uri_rule = URIRef(security + id_rule)
                rdf_graph.add((uri_children, security.RoleHasRules, uri_rule))
        return uri_children

    def parse_roles(rol_list: dict):
        if type(rol_list) is dict:
            id_rol = hashlib.sha224(rol_list["@xmi:id"].encode('utf-8')).hexdigest()
            uri_rol = URIRef(security + id_rol)
            rdf_graph.add((uri_database, security.DatabaseHasRole, uri_rol))
            rdf_graph.add((uri_rol, RDF.type, security.Role))
            rdf_graph.add(
                (uri_rol, RDFS.label, Literal(f"Role {rol_list['@name']}", datatype=XSD['string'])))
            rdf_graph.add(
                (uri_rol, security.roleName, Literal(rol_list['@name'], datatype=XSD['string'])))
            if '@rules' in rol_list:
                for rule in rol_list['@rules'].split(" "):
                    id_rule = hashlib.sha224(rule.encode('utf-8')).hexdigest()
                    uri_rule = URIRef(security + id_rule)
                    rdf_graph.add((uri_rol, security.RoleHasRules, uri_rule))
            if "children" in rol_list:
                for children in rol_list["children"]:
                    children_uri = parse_children(uri_rol, children)
                    while "children" in children:
                        children = children["children"]
                        if type(children) is dict:
                            parse_children(children_uri, children)
                        for chil in children:
                            if type(chil) is dict:
                                parse_children(children_uri, chil)

        else:
            for rol in rol_list:
                id_rol = hashlib.sha224(rol["@xmi:id"].encode('utf-8')).hexdigest()
                uri_rol = URIRef(security + id_rol)
                rdf_graph.add((uri_database, security.DatabaseHasRole, uri_rol))
                rdf_graph.add((uri_rol, RDF.type, security.Role))
                rdf_graph.add(
                    (uri_rol, RDFS.label, Literal(f"Role {rol['@name']}", datatype=XSD['string'])))
                rdf_graph.add(
                    (uri_rol, security.roleName, Literal(rol['@name'], datatype=XSD['string'])))
                if '@rules' in rol:
                    for rule in rol['@rules'].split(" "):
                        id_rule = hashlib.sha224(rule.encode('utf-8')).hexdigest()
                        uri_rule = URIRef(security + id_rule)
                        rdf_graph.add((uri_rol, security.RoleHasRules, uri_rule))
                if "children" in rol:
                    for children in rol["children"]:
                        children_uri = parse_children(uri_rol, children)
                        while "children" in children:
                            children = children["children"]
                            if type(children) is dict:
                                parse_children(children_uri, children)
                            for chil in children:
                                if type(chil) is dict:
                                    parse_children(children_uri, chil)

    def parse_privileges(rule, privileges: dict):
        if type(privileges) is dict:
            id_privilege = hashlib.sha224(privileges["@xmi:id"].encode('utf-8')).hexdigest()
            uri_privilege = URIRef(security + id_privilege)
            rdf_graph.add((uri_privilege, RDF.type, security[privileges["@xsi:type"]]))
            rdf_graph.add(
                (uri_privilege, RDFS.label, Literal(f"{privileges['@xsi:type']}", datatype=XSD['string'])))
            rdf_graph.add(
                (rule, security.SecurityRuleContainsPrivilege, uri_privilege))
            if '@condition' in privileges:
                rdf_graph.add(
                    (uri_privilege, security.privilegeCondition,
                     Literal(privileges['@condition'], datatype=XSD['string'])))
        else:
            for privilege in privileges:
                id_privilege = hashlib.sha224(privilege["@xmi:id"].encode('utf-8')).hexdigest()
                uri_privilege = URIRef(security + id_privilege)
                rdf_graph.add((uri_privilege, RDF.type, security[privilege["@xsi:type"]]))
                rdf_graph.add(
                    (uri_privilege, RDFS.label, Literal(f"{privilege['@xsi:type']}", datatype=XSD['string'])))
                rdf_graph.add(
                    (rule, security.SecurityRuleContainsPrivilege, uri_privilege))
                if '@condition' in privilege:
                    rdf_graph.add(
                        (uri_privilege, security.privilegeCondition,
                         Literal(privilege['@condition'], datatype=XSD['string'])))

    def parse_rules(rules: dict):
        for rule in rules:
            type_rule = rule["@xsi:type"]
            name_rule = rule["@name"]
            sign_rule = rule["sign"]["@xsi:type"]
            sign_rule = sign_rule.replace("possitive", "+").replace("negative", "-")
            id_rule = hashlib.sha224(rule["@xmi:id"].encode('utf-8')).hexdigest()
            uri_rule = URIRef(security + id_rule)
            rdf_graph.add((uri_database, security.DatabaseHasSecurityRule, uri_rule))
            rdf_graph.add(
                (uri_rule, security.ruleName, Literal(name_rule, datatype=XSD['string'])))
            rdf_graph.add(
                (uri_rule, security.ruleSign, Literal(sign_rule, datatype=XSD['string'])))

            parse_privileges(uri_rule, rule["privileges"])
            if type_rule == "SecurityRuleElement":
                rdf_graph.add((uri_rule, RDF.type, security.SecurityRuleElement))
                rdf_graph.add(
                    (uri_rule, RDFS.label, Literal(f"Rule Element {name_rule}", datatype=XSD['string'])))

                for element in rule['@elements'].split(" "):
                    id_element = hashlib.sha224(element.encode('utf-8')).hexdigest()
                    uri_element = URIRef(databases + id_element)
                    rdf_graph.add((uri_rule, security.SecurityRuleDefineElements, uri_element))

            elif type_rule == "SecurityRuleField":
                rdf_graph.add((uri_rule, RDF.type, security.SecurityRuleField))
                rdf_graph.add(
                    (uri_rule, RDFS.label, Literal(f"Rule Field {name_rule}", datatype=XSD['string'])))

                for field in rule['@fields'].split(" "):
                    id_field = hashlib.sha224(field.encode('utf-8')).hexdigest()
                    uri_field = URIRef(databases + id_field)
                    rdf_graph.add((uri_rule, security.SecurityRuleDefineField, uri_field))

    def parse_database(database: dict):
        id_database = hashlib.sha224(database["@xmi:id"].encode('utf-8')).hexdigest()
        global uri_database
        uri_database = URIRef(security + id_database)
        rdf_graph.add((uri_database, RDF.type, security.GraphsDatabase))
        rdf_graph.add(
            (uri_database, RDFS.label, Literal(f"Graph Database {database['@name']}", datatype=XSD['string'])))
        rdf_graph.add(
            (uri_database, databases.databaseHasName, Literal(database['@name'], datatype=XSD['string'])))

    def parse_users(users: dict):
        for user in users:
            user_name = user["@name"]
            id_user = hashlib.sha224(user["@xmi:id"].encode('utf-8')).hexdigest()
            uri_user = URIRef(security + id_user)

            rdf_graph.add((uri_user, RDF.type, security.User))
            rdf_graph.add(
                (uri_user, RDFS.label, Literal(f"User {user_name}", datatype=XSD['string'])))
            rdf_graph.add(
                (uri_user, security.userPass, Literal(user["@pass"], datatype=XSD['string'])))
            rdf_graph.add(
                (uri_user, security.userName, Literal(user_name, datatype=XSD['string'])))
            rdf_graph.add((uri_database, security.DatabaseHasUser, uri_user))

            for role in user['@roles'].split(" "):
                id_role = hashlib.sha224(role.encode('utf-8')).hexdigest()
                uri_role = URIRef(security + id_role)
                rdf_graph.add((uri_user, security.UserHasRole, uri_role))

    os.chdir("data")
    # Generate new graph
    rdf_graph = Graph()

    # Set up namespaces used
    databases = Namespace('https://w3id.org/OntoGraphDB/')
    bigowl = Namespace('http://ontologies.khaos.uma.es/bigowl/')
    security = Namespace('https://w3id.org/OntoSecurityGraphDB/')

    # Bind namespaces to prefix
    rdf_graph.bind('databases', databases)
    rdf_graph.bind('bigowl', bigowl)
    rdf_graph.bind('security', security)

    xml_dict = read_xml(filepath)

    if "Database" in xml_dict:
        parse_database(xml_dict["Database"])
        if "elements" in xml_dict["Database"]:
            elements_list = xml_dict["Database"]["elements"]
            parse_elements(elements_list)
        if "roles" in xml_dict["Database"]:
            roles_list = xml_dict["Database"]["roles"]
            parse_roles(roles_list)
        if "rules" in xml_dict["Database"]:
            rules_list = xml_dict["Database"]["rules"]
            parse_rules(rules_list)
        if "users" in xml_dict["Database"]:
            users_list = xml_dict["Database"]["users"]
            parse_users(users_list)

    visualize(rdf_graph)

    g = Graph()
    ontology_graph = g.parse("OntoGraphDB.owl")

    g = Graph()
    security_graph = g.parse("OntoSecurityGraphDB.owl")

    rdf_graph += security_graph + ontology_graph

    with open('graph.nt', 'w+', encoding='UTF-8') as f:
        f.write(rdf_graph.serialize(format='nt'))


if __name__ == "__main__":
    typer.run(parse2rdf)
