package view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class InGame {
    val gameTab = Tab("Jeu")
    var contentGrid = GridPane()

    init {


        val tabPane = TabPane()


        val reserveTab = Tab("Réserve")
        reserveTab.content = createReserveTabContent()
        tabPane.tabs.add(reserveTab)

    }


    fun updatePlayer(nom: String, pieces: Int, monuments: Int) {

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
            val cellNode =
                gridPane.children.find { GridPane.getRowIndex(it) == rowIndex && GridPane.getColumnIndex(it) == columnIndex }
            return cellNode == null
        }

        val hboxJoueur = HBox()
        val vboxInfoJoueur = VBox()
        vboxInfoJoueur.children.add(Label(nom))
        vboxInfoJoueur.children.add(Label("Argent : $bourse"))
        vboxInfoJoueur.children.add(Label("Monuments : $nbMonument/4"))

        hboxJoueur.children.add(vboxInfoJoueur)

        hboxJoueur.children.addAll(cartes)

        if (idJoueur == idJoueurAjoute) {
            contentGrid.add(hboxJoueur, 1, 5, 2, 1)

        } else {
            for (i in 1..3) {
                if (isGridPaneCellEmpty(contentGrid, i, 0)) {
                    contentGrid.add(hboxJoueur, 1, i, 2, 1)
                }
            }
        }

    }


    private fun createReserveTabContent(): StackPane {
        val reservePane = StackPane()
        reservePane.padding = Insets(10.0)
        val reserveLabel = Label("Réserve")
        reservePane.children.add(reserveLabel)

        return reservePane
    }
}
