class FineDrovaTest : GeneralTests<FineNode<Int, String>, FineSyncDrova<Int, String>>(
    treeFactory = { FineSyncDrova() }
)