
class OptimisticDrovaTest: GeneralTests<OptimisticNode<Int, String>, OptimisticDrova<Int, String>>(
    treeFactory = { OptimisticDrova() }
)