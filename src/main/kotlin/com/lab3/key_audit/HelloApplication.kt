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

    private var logFileName = ""
    private lateinit var userName: String
    private lateinit var textArea: TextArea
    private lateinit var toggleButton: ToggleButton
    private lateinit var fileChooserButton: Button
    private lateinit var userInput: TextField;



    override fun start(primaryStage: Stage) {
        textArea = TextArea()
        textArea.isEditable = false



        userInput = TextField()
        userInput.promptText = "имя пользователя"

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

        val headerPanel = VBox(10.0)
        headerPanel.children.addAll(fileChooserButton, userInput, toggleButton)



        // Обработчик для переключателя
        toggleButton.setOnAction {

            if (toggleButton.isSelected) {

                if(logFileName.isEmpty()){
                    textArea.appendText("Выберите файл для записи данных аудита\n")
                    toggleButton.isSelected = false
                    return@setOnAction
                }

                if(userInput.text.isEmpty()){
                    textArea.appendText("введите имя пользователя\n")
                    toggleButton.isSelected = false
                    return@setOnAction
                }

                textArea.isDisable = true
                userInput.isDisable = true;
                fileChooserButton.isDisable = true;

                textArea.appendText("Режим отслеживания клавиш включен.\n")
                textArea.appendText("Файл для записи: $logFileName\tИмя пользователя: ${userInput.text}\n")
                toggleButton.text = "Отключить отслеживание"
                // глобальный перехват клавиш
                registerNativeHook()
                addNativeKeyListener(this)
            } else {
                textArea.appendText("Режим отслеживания клавиш выключен.\n")
                textArea.isDisable = false
                userInput.isDisable = false;
                fileChooserButton.isDisable = false;
                toggleButton.text = "Включить отслеживание"
                textArea.appendText("Данные сохранены в файл: $logFileName")
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
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(HelloApplication::class.java)
        }
    }
}