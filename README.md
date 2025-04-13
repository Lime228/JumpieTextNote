# Jumpie TextNote

Текстовый редактор с голосовым вводом.

## ❓ Что есть:

- **Голосовой ввод** - реализован через библиотеку Vosk
- **Вкладки**
- **Масштабирование текста**
- **Изменение шрифта**

Note: Для работы голосового ввода требуется скачать модели Vosk и разместить их в voicemodels/. Модели можно найти на [официальном сайте Vosk](https://alphacephei.com/vosk/models).

## 🛠️ Сборка

Клонируйте репозиторий:
```bash
git clone https://github.com/Lime228/JumpieTextNote.git
```
Соберите проект:
```bash
mvn clean package
```
Запустите:

```bash
java -jar target/JumpieTextNote-1.0-SNAPSHOT-jar-with-dependencies.jar
```
