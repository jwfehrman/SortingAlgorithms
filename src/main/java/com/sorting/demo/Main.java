package com.sorting.demo;

import com.rits.cloning.Cloner;
import com.sorting.demo.algorithms.BubbleSort;
import com.sorting.demo.algorithms.InsertionSort;
import com.sorting.demo.algorithms.SelectionSort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    private Random rng = new Random();

    private ExecutorService exec = Executors.newFixedThreadPool(10);

    private static final Cloner CLONER = new Cloner();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        Series<String, Number> series = generateRandomIntegerSeries(150);

        BubbleSort bubbleSort = new BubbleSort(CLONER.deepClone(series));
        SelectionSort selectionSort = new SelectionSort(CLONER.deepClone(series));
        InsertionSort insertionSort = new InsertionSort(CLONER.deepClone(series));

        Button sort = new Button("Sort");

        Button reset = new Button("Reset");
//        reset.setOnAction(e -> chart.getData().set(0, generateRandomIntegerSeries(200)));

        HBox buttons = new HBox(5, sort, reset);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(5));

        sort.setOnAction(e -> {
//            buttons.setDisable(true);

            Task<Void> bubbleSortTask = bubbleSort.sort();
            Task<Void> selectionSortTask = selectionSort.sort();
            Task<Void> insertionSortTask = insertionSort.sort();
//            bubbleSortTask.setOnSucceeded(event -> buttons.setDisable(false));
            exec.submit(bubbleSortTask);
            exec.submit(selectionSortTask);
            exec.submit(insertionSortTask);
        });

        System.out.println(bubbleSort.getChart().getData().get(0).getData());
        System.out.println(selectionSort.getChart().getData().get(0).getData());

        GridPane gridPane = new GridPane();
        gridPane.add(bubbleSort.getChart(), 0, 0);
        gridPane.add(selectionSort.getChart(), 1, 0);
        gridPane.add(insertionSort.getChart(), 0, 1);
        BorderPane root = new BorderPane(gridPane);
        root.setBottom(buttons);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Series<String, Number> generateRandomIntegerSeries(int n) {
        Series<String, Number> series = new Series<>();
        for (int i = 1; i <= n; i++) {
            series.getData().add(new Data<>(Integer.toString(i), rng.nextInt(90) + 10));
        }
        return series;
    }
}