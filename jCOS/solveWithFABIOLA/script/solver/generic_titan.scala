    val PRECIO = in("PRECIO").asInstanceOf[Seq[Int]]

    val PERCENTAGES: Seq[Int] = in("PERCENTAGES").asInstanceOf[Seq[Int]]

    val CONSUMOS = in("C").asInstanceOf[Seq[Seq[Int]]]
    /** ***********************************************************
     * Variables
     * ***********************************************************/
    //val limiteInferiorPotenciaContratada = 1500
    val limiteInferiorPotenciaContratada = 1000
    val limiteSuperiorPotenciaContratada = 45000
    val limiteSuperiorTP = 10000000


    val TP = model.intVarArray("gasto mensual", CONSUMOS.length, IntVar.MIN_INT_BOUND, limiteSuperiorTP)
    val TPTotal = model.intVar("Coste total", IntVar.MIN_INT_BOUND, limiteSuperiorTP)

    val potenciaContratada = model.intVarArray("Potencias contratadas", 3, 0, limiteSuperiorTP)

    val potenciaConsumida = model.intVarMatrix("PM", CONSUMOS.length, 3, 0, limiteSuperiorTP)
    val potenciaFactura = model.intVarMatrix("Potencia Factura", CONSUMOS.length, 3, 0, limiteSuperiorTP)
    val terminoPotencia = model.intVarMatrix("Termino Potencia", CONSUMOS.length, 3, 0, limiteSuperiorTP)

    potenciaContratada(0) = model.intVar("Potencia Contratada 1", limiteInferiorPotenciaContratada, limiteSuperiorPotenciaContratada)
    potenciaContratada(1) = model.intVar("Potencia Contratada 2", limiteInferiorPotenciaContratada, limiteSuperiorPotenciaContratada)
    potenciaContratada(2) = model.intVar("Potencia Contratada 3", limiteInferiorPotenciaContratada, limiteSuperiorPotenciaContratada)


    /** ***********************************************************
      * Restricciones https://tarifaluzhora.es/info/maximetro
      * ***********************************************************/

    // For now it will receive no more than 2 thresholds and 3 prices

    var lower_thr = 85
    var upper_thr = 105

    if (PERCENTAGES.length < 2) {
      println("WARNING: At least 2 thresholds are needed. Using 85 and 105 as default")
    } else if (PERCENTAGES.length > 2) {
      println("WARNING: No more than 2 thresholds are allowed. Using the first 2 as default")
      lower_thr = PERCENTAGES(0)
      upper_thr = PERCENTAGES(1)
    } else {
      lower_thr = PERCENTAGES(0)
      upper_thr = PERCENTAGES(1)
    }

    for (i <- CONSUMOS.indices) {
      for (j <- 0 until 3) {

        val pm = CONSUMOS(i)(j)
        model.arithm(potenciaConsumida(i)(j), "=", pm).post()

        val precio: Int = PRECIO(j)

        model.ifThen(
          model.arithm(model.intScaleView(potenciaConsumida(i)(j), 100), "<=", model.intScaleView(potenciaContratada(j), lower_thr)),
          model.arithm(model.intScaleView(potenciaFactura(i)(j), 100), "=", model.intScaleView(potenciaContratada(j), lower_thr))
        )

        model.ifThen(
          model.and(
            model.arithm(model.intScaleView(potenciaConsumida(i)(j), 100), ">", model.intScaleView(potenciaContratada(j), lower_thr)),
            model.arithm(model.intScaleView(potenciaConsumida(i)(j), 100), "<", model.intScaleView(potenciaContratada(j), upper_thr))
          ),
          model.arithm(model.intScaleView(potenciaFactura(i)(j), 100), "=", model.intScaleView(potenciaConsumida(i)(j), 100))
        )

        val exceso = model.sum(s"Exceso[$i][$j]",
          model.intMinusView(model.intScaleView(potenciaContratada(j), upper_thr)),
          model.intScaleView(potenciaConsumida(i)(j), 100)
        )
        val sumaYDobleExceso = model.sum(
          s"SumaYDobleExceso[$i][$j]",
          model.intScaleView(potenciaContratada(j), 100),
          exceso
        )

        model.ifThen(
          model.arithm(model.intScaleView(potenciaConsumida(i)(j), 100), ">=", model.intScaleView(potenciaContratada(j), upper_thr)),
          model.arithm(model.intScaleView(potenciaFactura(i)(j), 100), "=", sumaYDobleExceso )
        )

        model.times(potenciaFactura(i)(j), precio, terminoPotencia(i)(j)).post() // x * a = z
      }
    }

    // Calculo de TPi
    for (i <- 0 until TP.length)
      model.sum(terminoPotencia(i), "=", TP(i)).post()

    /** ***********************************************************
      * Objetivo
      * ***********************************************************/
    model.sum(TP, "=", TPTotal).post()
    model.setObjective(Model.MINIMIZE, TPTotal)

    /** ***********************************************************
      * Solucion
      * ***********************************************************/
    val solver = model.getSolver
    val solution = solver.findOptimalSolution(TPTotal, Model.MINIMIZE, new TimeCounter(model, 5 * 1000000000L))
    //val solution = solver.findOptimalSolution(TPTotal, Model.MINIMIZE)

    val metrics = Seq(solver.getTimeCount.toDouble,solver.getReadingTimeCount.toDouble, (solver.getTimeCount + solver.getReadingTimeCount).toDouble, model.getNbVars.toDouble, model.getNbCstrs.toDouble)

    // solver.printStatistics()
    //throw solver.getContradictionException

    if(solution != null){
      ModelOutput(
        Seq(
          solution.getIntVal(TPTotal),
          solution.getIntVal(potenciaContratada(0)),
          solution.getIntVal(potenciaContratada(1)),
          solution.getIntVal(potenciaContratada(2))
        ),
        metrics
      )
    } else {
      ModelOutput(
        Seq(
          -1.0/*Default.DefaultDouble.default*/,
          -1.0/*Default.DefaultDouble.default*/,
          -1.0/*Default.DefaultDouble.default*/,
          -1.0/*Default.DefaultDouble.default*/
        ),
        metrics
      )
    }