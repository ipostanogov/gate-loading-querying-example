gate-loading-querying-example
========

Запуск в IntelliJ IDEA
------------------------
* убедиться, что в пути java `System.getProperty(“user.home”)` все символы латинские;
* скачать и установить [sbt](http://www.scala-sbt.org/0.12.2/docs/Getting-Started/Setup.html), [jdk](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html);
* проверить, что переменная [JAVA_HOME](http://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/index.html) указывает на папку с jdk
* зайти в папку с исходниками из консоли;
* выполнить sbt
* выполнить gen-idea no-classifiers
* скачать и установить [IDEA](http://www.jetbrains.com/idea/download/index.html);
* установить Scala-плагин по разделу [Setting up the Environment](http://confluence.jetbrains.com/display/SCA/Getting+Started+with+IntelliJ+IDEA+Scala+Plugin);
* открыть сгенерированный проект (папка с исходниками) в IDEA;
* открыть `src\main\scala\InformationRetrievalApp.scala`;
* щелкнуть [ПКМ](http://ru.wikipedia.org/wiki/Щелчок_%28нажатие_клавиши%29#.D0.9F.D1.80.D0.B0.D0.B2.D0.B0.D1.8F_.D0.BA.D0.BD.D0.BE.D0.BF.D0.BA.D0.B0) по `InformationRetrievalApp` в строке `object InformationRetrievalApp {`;
* щелкнуть на Debug.