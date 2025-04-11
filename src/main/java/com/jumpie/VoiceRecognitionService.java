package com.jumpie;

import javax.sound.sampled.*;

import org.vosk.*;

import javax.swing.*;
import java.io.File;

public class VoiceRecognitionService {
    private Recognizer recognizer;
    private volatile TargetDataLine microphone;
    private volatile boolean isListening = false;
    private JFrame parentFrame;
    private Runnable onStateChange;
    private AudioFormat format;

    public VoiceRecognitionService(JFrame frame, String modelPath) {
        this.parentFrame = frame;
        this.format = new AudioFormat(16000, 16, 1, true, false);
        initializeModel(modelPath);
    }

    private void initializeModel(String modelPath) {
        try {
            File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                showError("Папка с моделью не найдена: " + modelPath);
                return;
            }
            Model model = new Model(modelPath);
            recognizer = new Recognizer(model, 16000);
        } catch (Exception e) {
            showError("Ошибка загрузки модели: " + e.getMessage());
        }
    }

    public synchronized void toggleRecognition(TextAppender appender) {
        if (isListening) {
            stopRecognition();
        } else {
            startRecognition(appender);
        }
    }

    private void startRecognition(TextAppender appender) {
        if (isListening) return;

        new Thread(() -> {
            try {
                synchronized (this) {
                    if (microphone != null) {
                        closeMicrophone();
                    }

                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    if (!AudioSystem.isLineSupported(info)) {
                        showError("Микрофон не поддерживает нужный формат");
                        return;
                    }

                    microphone = (TargetDataLine) AudioSystem.getLine(info);
                    microphone.open(format);
                    microphone.start();
                    isListening = true;
                    notifyStateChanged();
                }

                byte[] buffer = new byte[4096];
                while (isListening) {
                    TargetDataLine currentMic;
                    synchronized (this) {
                        currentMic = microphone;
                    }

                    if (currentMic != null) {
                        int bytesRead = currentMic.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            processAudio(buffer, bytesRead, appender);
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (Exception e) {
                showError("Ошибка распознавания: " + e.getMessage());
            } finally {
                synchronized (this) {
                    closeMicrophone();
                    isListening = false;
                    notifyStateChanged();
                    if (recognizer != null) {
                        String finalResult = recognizer.getFinalResult();
                        if (finalResult != null) {
                            appender.appendText(extractTextFromResult(finalResult));
                        }
                    }
                }
            }
        }).start();
    }

    public synchronized void stopRecognition() {
        if (!isListening) return;

        isListening = false;
        closeMicrophone();
        notifyStateChanged();
    }

    private void processAudio(byte[] buffer, int bytesRead, TextAppender appender) {
        if (recognizer.acceptWaveForm(buffer, bytesRead)) {
            String result = recognizer.getResult();
            if (result != null && !result.trim().equals("{\"text\" : \"\"}")) {
                appender.appendText(extractTextFromResult(result));
            }
        }
    }

    private synchronized void closeMicrophone() {
        try {
            if (microphone != null) {
                microphone.stop();
                microphone.close();
                microphone = null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии микрофона: " + e.getMessage());
        }
    }

    private String extractTextFromResult(String jsonResult) {
        if (jsonResult == null) return "";

        int textIndex = jsonResult.indexOf("\"text\" : \"") + 10;
        if (textIndex >= 10) {
            int endIndex = jsonResult.indexOf("\"", textIndex);
            if (endIndex > textIndex) {
                return jsonResult.substring(textIndex, endIndex) + " ";
            }
        }
        return "";
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(parentFrame, message));
    }

    private void notifyStateChanged() {
        if (onStateChange != null) {
            SwingUtilities.invokeLater(onStateChange);
        }
    }

    public synchronized boolean isListening() {
        return isListening;
    }

    public void setOnStateChangeListener(Runnable listener) {
        this.onStateChange = listener;
    }

    public synchronized void dispose() {
        stopRecognition();
        if (recognizer != null) {
            recognizer.close();
        }
    }
}