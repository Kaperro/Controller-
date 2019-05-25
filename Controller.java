package compilador;

import grammar.Yytoken;
import java_cup.runtime.Symbol;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static compilador.files.FileHandling.OpenFile;
import static compilador.files.FileHandling.SaveFile;
import static compilador.files.FileHandling.SaveFileAs;

public class Controller {
    public CodeArea CodeArea_Main;
    public TextArea Result;
    public TableView TableView_Tokens;
    public TableColumn Column_Number;
    public TableColumn Column_Token;
    public TableColumn Column_Type;
    public TableColumn Column_Column;
    public TableColumn Column_Line;
    private File OpenedFile;

    public void initialize() {
        CodeArea_Main.setParagraphGraphicFactory(LineNumberFactory.get(CodeArea_Main));
        Column_Number.setCellValueFactory(new PropertyValueFactory<Yytoken, Integer>("NumToken"));
        Column_Token.setCellValueFactory(new PropertyValueFactory<Yytoken, String>("Token"));
        Column_Type.setCellValueFactory(new PropertyValueFactory<Yytoken, String>("Tipo"));
        Column_Column.setCellValueFactory(new PropertyValueFactory<Yytoken, Integer>("Columna"));
        Column_Line.setCellValueFactory(new PropertyValueFactory<Yytoken, Integer>("Linea"));
    }

    public void ActionOpenFile() {
        OpenedFile = OpenFile();
        if(OpenedFile != null){
            Result.appendText("Abierto archivo: " + OpenedFile.getAbsolutePath());
            Result.appendText("\n");
            CodeArea_Main.clear();
            StringBuilder readlines = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(OpenedFile));
            } catch (IOException | NullPointerException ignored) {
                new Alert(Alert.AlertType.ERROR, "Error al abrir el archivo.").showAndWait();
            }
            try {
                String Line;
                if (bufferedReader != null) {
                    while ((Line = bufferedReader.readLine()) != null)
                        readlines.append(Line).append("\n");
                }
            } catch (IOException | NullPointerException ignored) {
                new Alert(Alert.AlertType.ERROR, "Error al abrir el archivo.").showAndWait();
            }
            CodeArea_Main.appendText(readlines.toString());
        }
    }

    public void ActionSaveFile() {
        if(OpenedFile != null){
            SaveFile(OpenedFile, CodeArea_Main.getText().replace("\n", "\r\n"));
            Result.appendText("Guardado archivo: " + OpenedFile.getAbsolutePath());
            Result.appendText("\n");
        } else {
            ActionSaveToNewFile();
        }
    }

    public void ActionSaveToNewFile() {
        SaveFileAs(CodeArea_Main.getText().replace("\n", "\r\n"));
        Result.appendText("Guardado archivo como: " + OpenedFile.getAbsolutePath());
        Result.appendText("\n");
    }

    public void ActionCompile() {
        File file = new File("src/grammar/Lexer.flex");
        jflex.Main.generate(file);

        GetTokens();
    }

    private void GetTokens(){
        Result.appendText("Buscando Tokens");
        Result.appendText("\n");
        String tempNameFile = "temp.cpp";
       try {
            Files.write(Paths.get(tempNameFile), CodeArea_Main.getText().replace("\n","\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Symbol> yytokens = new ArrayList<>();
        /*try (BufferedReader bf = new BufferedReader(new FileReader(tempNameFile))) {
            AnalizadoLexico a = new AnalizadoLexico(bf);
            Symbol token;
            do {
                token = a.nextToken();
                if(token!=null){
                    yytokens.add(token);
                    Result.appendText(token.toString());
                    Result.appendText("\n");
                    System.out.println(token);
                }
            } while (token != null);
        } catch (Exception ignored) {
        }*/
        if(yytokens.isEmpty()){
            Result.appendText("Sin tokens");
            Result.appendText("\n");
        } else{
            TableView_Tokens.getItems().setAll(yytokens);
        }

    }

}
