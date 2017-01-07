package quantizer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainWindowController {
    public Label programStatus = new Label();     // Successful or unsuccessful.
    public Hyperlink sourceFileInfo = new Hyperlink();          // Source file info (name, size).
    public Hyperlink outputFileInfo = new Hyperlink();    // Output file location.
    public TextField sourceLocation = new TextField();    // Source file location.
    public Button compress_btn = new Button("Compress");
    public Button decompress_btn = new Button("Decompress");
    public Button browse_btn = new Button("Browse");
    public ComboBox<Integer> nBits = new ComboBox<>(); // the number of quantization levels.

    File sourceFile;


    @FXML
    public void onFileChooserButtonClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file");
        sourceFile =fileChooser.showOpenDialog(new Stage());
        if (sourceFile!=null) {
            sourceFileInfo.setText(sourceFile.getName() + " (" + sourceFile.length()/1024 + " KB)");
            sourceLocation.setText(sourceFile.getAbsolutePath());
        }
    }

    @FXML
    public void onCompressButtonClicked(){
        if (sourceFile != null && nBits.getSelectionModel().getSelectedItem() != null){
            quantizer.Image image = new quantizer.Image();
            int [][] pixels = image.readImage(sourceFile);
            Quantizer quantizer = new Quantizer();
            String compressedFileLocation = sourceFile.getParent() + "/compressed image.dat";

            try{
                quantizer.compress(nBits.getValue() , pixels , image.width , image.height ,
                        compressedFileLocation);
            }
            catch (IOException e){
                displayErrorDialg(e.getMessage());
            }

            programStatus.setText("Compression Successful!!");
            outputFileInfo.setText(Paths.get(compressedFileLocation).toString());

            // save them for the decompress (inorder to make the program closable)

            try {
                FileOutputStream fout = new FileOutputStream(sourceFile.getParent() + "/App Data.ser" , false);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(image.height);
                oos.writeObject(image.width);
                oos.writeObject(quantizer.getAveragesList());
                oos.close();
                fout.close();
            } catch (IOException e) {
                //e.printStackTrace();
                displayErrorDialg(e.getMessage());
            }
        }else{
            if (sourceFile == null){
                displayErrorDialg("Please Choose a File First.");
            }else if (nBits.getSelectionModel().getSelectedItem() == null){
                displayErrorDialg("Please Choose the Number of Bits First");
            }
        }
    }

    @FXML
    public void onDecompressButtonClicked(){
        if(sourceFile !=null){
            int height = 0;
            int width = 0;
            ArrayList<Integer> averagesList = new ArrayList<>();

            // read the above objects & variables
            try {
                FileInputStream fin = new FileInputStream(sourceFile.getParent() + "/App Data.ser");
                ObjectInputStream ois = new ObjectInputStream(fin);
                height = (int)ois.readObject();
                width = (int)ois.readObject();
                averagesList = (ArrayList<Integer>)ois.readObject();
                ois.close();
                fin.close();
            } catch (Exception e) {
                //e.printStackTrace();
                displayErrorDialg(e.getMessage());
            }

            Quantizer quantizer = new Quantizer();
            quantizer.setAveragesList(averagesList);
            int [][] dequantizedPixels = null;
            try{
                dequantizedPixels = quantizer.decompress(sourceFile , width , height);
             }
             catch (FileNotFoundException e){
                displayErrorDialg(e.getMessage());
             }
            quantizer.Image image = new quantizer.Image();
            File dequantizedImage = new File(sourceFile.getParent() + "/" + "dequantized image.jpg");
            image.writeImage(dequantizedPixels , dequantizedImage , width , height);

            programStatus.setText("Decompression Successful!!");
            outputFileInfo.setText(dequantizedImage.getAbsolutePath());
        }else{
            displayErrorDialg("Please choose a file first.");
        }
    }

    // Handle Menu Items Actions
    @FXML
    public void onResetClicked(){
        programStatus.setText("");
        sourceFileInfo.setText("");
        outputFileInfo.setText("");
        sourceLocation.setText("Select File...");
        nBits.getSelectionModel().clearSelection();
        sourceFile = null;
    }

    @FXML
    public void onCloseClicked(){
        Platform.exit();
    }

    @FXML
    public void onAboutClicked(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("about.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root, 275, 150));
            stage.getIcons().add(
                    new Image(
                            MainWindowController.class.getResourceAsStream( "icon.png" )));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
        }catch(Exception e){
            //e.printStackTrace();
            displayErrorDialg(e.getMessage());
        }
    }


    private void displayErrorDialg(String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //alert.setTitle("Error Dialog");
        alert.setHeaderText(null);  // setting to null puts no header
        alert.setContentText(contentText);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(
                        MainWindowController.class.getResourceAsStream( "icon.png" )));
        alert.showAndWait();
    }

    @FXML
    private void openSourceFile(ActionEvent event){
        openFile(sourceFile.getAbsolutePath());
    }

    @FXML
    private void openOutputFile(ActionEvent event){
        openFile(((Hyperlink)event.getSource()).getText());
    }


    private void openFile(String filePath){
        try{
            Desktop.getDesktop().open(new File (filePath));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
