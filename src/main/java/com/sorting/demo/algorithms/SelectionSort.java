package com.sorting.demo.algorithms;

import com.sorting.demo.common.Constants;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

import java.util.concurrent.CountDownLatch;

public class SelectionSort {
    private BarChart<String, Number> chart;
    private XYChart.Series<String, Number> series;

    public SelectionSort(XYChart.Series<String, Number> series) {
        this.series = series;
        init();
    }

    private void init() {
        chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setAnimated(false);
        chart.setTitle("Selection Sort");
        chart.getData().add(series);
    }

    public BarChart<String, Number> getChart() {
        return chart;
    }

    public XYChart.Series<String, Number> getSeries() {
        return series;
    }

    public Task<Void> sort() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<XYChart.Data<String, Number>> data = series.getData();
                int n = data.size();

                // One by one move boundary of unsorted subarray
                for (int i = 0; i < n; i++) {
                    // Find the minimum element in unsorted array
                    int min_idx = i;
                    for (int j = i + 1; j < n; j++) {
                        if (data.get(j).getYValue().intValue() < data.get(min_idx).getYValue().intValue()) {
                            min_idx = j;
                        }
                    }

                    XYChart.Data<String, Number> first = data.get(i);
                    XYChart.Data<String, Number> second = data.get(min_idx);

                    Platform.runLater(() -> {
                        first.getNode().setStyle("-fx-background-color: green ;");
                        second.getNode().setStyle("-fx-background-color: green ;");
                    });

                    // Swap the found minimum element with the first
                    // element
                    if (first.getYValue().intValue() > second.getYValue().intValue()) {
                        CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            Animation swap = createSwapAnimation(first, second);
                            swap.setOnFinished(e -> latch.countDown());
                            swap.play();
                        });
                        latch.await();
                    }

                    Platform.runLater(() -> {
                        first.getNode().setStyle("");
                        second.getNode().setStyle("");
                    });
                }

                return null;
            }
        };
    }

    private <T> Animation createSwapAnimation(XYChart.Data<?, T> first, XYChart.Data<?, T> second) {
        double firstX = first.getNode().getParent().localToScene(first.getNode().getBoundsInParent()).getMinX();
        double secondX = first.getNode().getParent().localToScene(second.getNode().getBoundsInParent()).getMinX();

        double firstStartTranslate = first.getNode().getTranslateX();
        double secondStartTranslate = second.getNode().getTranslateX();

        TranslateTransition firstTranslate = new TranslateTransition(Duration.millis(Constants.TIME), first.getNode());
        firstTranslate.setByX(secondX - firstX);
        TranslateTransition secondTranslate = new TranslateTransition(Duration.millis(Constants.TIME), second.getNode());
        secondTranslate.setByX(firstX - secondX);
        ParallelTransition translate = new ParallelTransition(firstTranslate, secondTranslate);

        translate.statusProperty().addListener((obs, oldStatus, newStatus) -> {
            if (oldStatus == Animation.Status.RUNNING) {
                T temp = first.getYValue();
                first.setYValue(second.getYValue());
                second.setYValue(temp);
                first.getNode().setTranslateX(firstStartTranslate);
                second.getNode().setTranslateX(secondStartTranslate);
            }
        });

        return translate;
    }
}
