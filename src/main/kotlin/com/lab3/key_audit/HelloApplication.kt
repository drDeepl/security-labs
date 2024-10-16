package com.lab3.key_audit

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.GlobalScreen.addNativeKeyListener
import com.github.kwhat.jnativehook.GlobalScreen.registerNativeHook
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.FileWriter
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.GroupLayout.Alignment

class HelloApplication : Application(), NativeKeyListener {

    private var logFileName = "key_log.txt"
    private lateinit var textArea: TextArea
    private lateinit var toggleButton: ToggleButton
    private lateinit var fileChooserButton: Button
    private lateinit var userInput: TextField;

    override fun start(primaryStage: Stage) {
        textArea = TextArea()
        textArea.isEditable = false



        userInput = TextField("имя пользователя")

        toggleButton = ToggleButton("Включить отслеживание клавиш")

        fileChooserButton = Button("Выбрать файл для записи")

        fileChooserButton.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Выберите файл для записи"
            fileChooser.initialFileName = "key_log.txt"
            val selectedFile = fileChooser.showSaveDialog(primaryStage)
            if (selectedFile != null) {
                logFileName = selectedFile.absolutePath
                textArea.appendText("Файл для записи выбран: $logFileName\n")
            }
        }

        userInput.focusedProperty().addListener { _, _, isFocused ->
            if (isFocused) {
                userInput.clear()
            }
        }



        val headerPanel = VBox(10.0)
        headerPanel.children.addAll(toggleButton, userInput,fileChooserButton )



        // Обработчик для переключателя
        toggleButton.setOnAction {
            if (toggleButton.isSelected) {
                textArea.appendText("Режим отслеживания клавиш включен.\n")
                toggleButton.text = "Отключить отслеживание"
                // глобальный перехват клавиш
                registerNativeHook()
                addNativeKeyListener(this)
            } else {
                textArea.appendText("Режим отслеживания клавиш выключен.\n")
                toggleButton.text = "Включить отслеживание"
                // Отключить глобальный перехват клавиш
                GlobalScreen.unregisterNativeHook()
            }
        }

        val root = VBox(headerPanel, textArea)
        val scene = Scene(root, 400.0, 400.0)

        primaryStage.title = "Аудит клавиатуры"
        primaryStage.scene = scene
        primaryStage.show()

        // Выключить логирование клавиш
        Logger.getLogger(GlobalScreen::class.java.getPackage().name).level = Level.OFF
    }

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        if (toggleButton.isSelected) {
            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val keyCode = NativeKeyEvent.getKeyText(event.keyCode)
            val logMessage = "[${currentTime.format(formatter)}] [${userInput.text}] Нажата клавиша $keyCode\n"
            textArea.appendText(logMessage)
            logKeyPress(logMessage)
        }
    }

    override fun nativeKeyReleased(event: NativeKeyEvent) {}

    override fun nativeKeyTyped(event: NativeKeyEvent) {}

    private fun logKeyPress(message: String) {
        // Записываем сообщение в файл
        try {
            FileWriter(logFileName, true).use { writer ->  // true для добавления в конец файла
                writer.write(message)
            }
        } catch (e: IOException) {
            e.printStackTrace() // В случае ошибки выводим стек трейс
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(HelloApplication::class.java)
        }
    }
}