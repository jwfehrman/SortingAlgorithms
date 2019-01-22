package com.sorting.demo.algorithms;

import com.sorting.demo.common.Constants;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

public class MergeSort {
    private BarChart<String, Number> chart;
    private XYChart.Series<String, Number> series;

    public MergeSort(XYChart.Series<String, Number> series) {
        this.series = series;
        init();
    }

    private void init() {
        chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setAnimated(false);
        chart.setTitle("Bubble Sort");
        chart.getData().add(series);
    }

    public BarChart<String, Number> getChart() {
        return chart;
    }

    public XYChart.Series<String, Number> getSeries() {
        return series;
    }

    private void merge(int l, int m, int r) {
        ObservableList<XYChart.Data<String, Number>> L = series.getData();
        ObservableList<XYChart.Data<String, Number>> R = series.getData();

        int n1 = m - l + 1;
        int n2 = r - m;

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i)
            L.set(i, series.getData().get(l + i));
        for (int j = 0; j < n2; ++j)
            R.set(j, series.getData().get(m + 1 + j));


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2) {
            if (L.get(i).getYValue().intValue() <= R.get(j).getYValue().intValue()) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    public Task<Void> sort() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<XYChart.Data<String, Number>> data = series.getData();

                int n = series.getData().size();

                if (n < 2) {
                    return null;
                }
                int mid = n / 2;
                int[] l = new int[mid];
                int[] r = new int[n - mid];

                for (int i = 0; i < mid; i++) {
                    l[i] = a[i];
                }
                for (int i = mid; i < n; i++) {
                    r[i - mid] = a[i];
                }
                mergeSort(l, mid);
                mergeSort(r, n - mid);

                merge(a, l, r, mid, n - mid);
                return null;
            }
        };
    }

    public Task<Void> sort() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<XYChart.Data<String, Number>> data = series.getData();

                int n = series.getData().size();

                if (n < 2) {
                    return;
                }
                int mid = n / 2;
                int[] l = new int[mid];
                int[] r = new int[n - mid];

                for (int i = 0; i < mid; i++) {
                    l[i] = a[i];
                }
                for (int i = mid; i < n; i++) {
                    r[i - mid] = a[i];
                }
                mergeSort(l, mid);
                mergeSort(r, n - mid);

                merge(a, l, r, mid, n - mid);
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