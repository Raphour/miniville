package view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color


class InGame : TabPane() {
    private var contentGrid = GridPane()

    private var vboxLog = VBox()


    private var logPane: ScrollPane


    init {
        addToLog("Baguette ouiouioui trop bon la baguette sa mere")
        addToLog("Le pain")
        addToLog("Le fromage")


        logPane = ScrollPane(vboxLog)






        logPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER


        logPane.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS


        contentGrid.add(logPane, 5, 0, 1, 2)

        // Définir le nombre de lignes
        for (row in 0 until 5) {
            val rowConstraints = RowConstraints()
            rowConstraints.percentHeight = 20.0 // Définir la hauteur de chaque ligne
            contentGrid.rowConstraints.add(rowConstraints)
        }

        // Définir le nombre de colonnes
        for (col in 0 until 6) {
            val colConstraints = ColumnConstraints()
            colConstraints.percentWidth = 16.66666667 // Définir la largeur de chaque colonne
            contentGrid.columnConstraints.add(colConstraints)
        }



        val gameTab = Tab("Jeu")
        gameTab.content = contentGrid
        gameTab.isClosable = false

        val reserveTab = Tab("Réserve")
        reserveTab.isClosable = false
        reserveTab.content = createReserveTabContent()
        this.tabs.add(gameTab)
        this.tabs.add(reserveTab)


    }


    fun updatePlayer(nom: String, pieces: Int, monuments: Int) {
        TODO()
    }


    private fun createPlayerPane(playerName: String, pieces: Int, monuments: Int): VBox {
        val playerLabel = Label(playerName)
        val piecesLabel = Label("Pieces: $pieces")
        val monumentsLabel = Label("Monuments: $monuments")

        val playerPane = VBox(10.0, playerLabel, piecesLabel, monumentsLabel)
        playerPane.alignment = Pos.CENTER

        return playerPane
    }

    fun addPlayerToGrid(
        nom: String,
        nbMonument: Int,
        idJoueur: Int,
        idJoueurAjoute: Int,
        bourse: Int,
        cartes: MutableList<ImageView>
    ) {
        fun isGridPaneCellEmpty(gridPane: GridPane, rowIndex: Int, columnIndex: Int): Boolean {
            val cellNode = gridPane.children.find {
                GridPane.getRowIndex(it) == rowIndex && GridPane.getColumnIndex(it) == columnIndex
            }
            return cellNode == null
        }

        val hboxJoueur = HBox()
        val vboxInfoJoueur = VBox()
        if (idJoueur == idJoueurAjoute) {
            vboxInfoJoueur.children.add(Label("$nom (vous)"))
        } else {
            vboxInfoJoueur.children.add(Label(nom))
        }
        vboxInfoJoueur.children.add(Label("Argent : $bourse"))
        vboxInfoJoueur.children.add(Label("Monuments : $nbMonument/4"))

        hboxJoueur.children.add(vboxInfoJoueur)

        hboxJoueur.children.addAll(cartes)

        println("$idJoueur $idJoueurAjoute")
        if (idJoueur == idJoueurAjoute) {
            println("MEME ID")
            contentGrid.add(hboxJoueur, 2, 4, 2, 1)

        } else {
            var rowIndex = 1
            for (i in 1..3) {

                if (isGridPaneCellEmpty(contentGrid, i, 0)) {
                    rowIndex = i
                    break
                }
            }
            contentGrid.add(hboxJoueur, 0, rowIndex, 2, 1)
        }

    }


    private fun createReserveTabContent(): StackPane {
        val reservePane = StackPane()
        reservePane.padding = Insets(10.0)
        val reserveLabel = Label("Réserve")
        reservePane.children.add(reserveLabel)

        return reservePane
    }

    fun addToLog(message: String) {
        val label = Label(message)
        label.maxWidth = Double.MAX_VALUE
        VBox.setVgrow(label, Priority.ALWAYS)
        label.isWrapText = true
        vboxLog.children.add(label)
        if (vboxLog.children.size % 2 == 0) {
            label.background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY))
        } else {
            label.background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
        }

    }
}
