package com.lab3.key_audit

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.layout.VBox
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(primaryStage: Stage) {
        val textArea = TextArea()
        textArea.isEditable = false
        textArea.isEditable = false

        val toggleButton = ToggleButton("Включить отслеживание клавиш")

        // Обработчик нажатий клавиш
        textArea.setOnKeyPressed { event ->
            if (toggleButton.isSelected) {  // Проверяем, активен ли переключатель
                val keyCode = event.code
                textArea.appendText("Нажата клавиша: ${keyCode.name}\n")
            }
        }

        // Обработчик для переключателя
        toggleButton.setOnAction {
            if (toggleButton.isSelected) {
                textArea.appendText("Режим отслеживания клавиш включен.\n")
            } else {
                textArea.appendText("Режим отслеживания клавиш выключен.\n")
            }
        }

        val root = VBox(toggleButton, textArea)
        val scene = Scene(root, 400.0, 300.0)

        primaryStage.title = "Key Press Tracker"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}