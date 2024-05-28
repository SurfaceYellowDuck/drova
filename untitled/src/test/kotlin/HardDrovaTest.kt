class HardDrovaTest : GeneralTests<HardNode<Int, String>, HardSyncDrova<Int, String>>(
    treeFactory = { HardSyncDrova() }
)
