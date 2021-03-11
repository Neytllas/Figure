package sample;

import com.sun.beans.editors.ColorEditor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.control.Label;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    public Canvas mainCanvas;
    public Slider  sldA;
    public Slider sldZoom;
    public Label messageLbl;
    public AnchorPane mainPane;

    // сдвиг по x y
    double offsetX = 0;
    double offsetY = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        draw();

        sldA.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            draw();
        }
                );

        sldZoom.valueProperty().addListener((observable, oldValue, newValue) ->
                {
                    draw();
                }
        );

        // резиновость окна
        mainCanvas.widthProperty().bind(mainPane.widthProperty().subtract(312));
        mainCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.draw();
        });

    }

    void draw()
    {
        // высчитывание дпи

        double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        double zoom= dpi / 2.54 * sldZoom.getValue() / 100;

        GraphicsContext ctx = mainCanvas.getGraphicsContext2D();

        ctx.setFill(Color.WHITE);
        ctx.fillRect(0,0, mainCanvas.getWidth() + offsetX, mainCanvas.getHeight() + offsetY);

        //сохранение состояния матроицы
        ctx.save();

        // запрос матрицы преобразования
        Affine transform = ctx.getTransform();

        //отрисовка фигуры в центре экрана
        transform.appendTranslation(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2);
        //увеличение масштаба
        transform.appendScale(zoom, -zoom);
        //присваиваем обратно
        ctx.setTransform(transform);

        // объявление переменных для рассчета

        double a = sldA.getValue();


        double c = (a * (Math.sqrt(3) / 2));

        double d1 = 2 * a;
        double d2 = a * Math.sqrt(3);

        double r = (d1 * d2) / Math.sqrt((Math.pow(d1,2))+ (Math.pow(d2,2))*8);

        double d = 2 * r;

        double S1 = (d1*d2)/2;

        double S2 = Math.PI * Math.pow(r,2);

        double S = S1 - S2;

        //double b = Math.sqrt((Math.pow(a,2) + Math.pow((a * Math.sqrt(3) / 2) , 2)));
        messageLbl.setText("Площадь выделеной области: "+ Math.round(S) + " см");


        //толщина линии
        ctx.setLineWidth(2. / zoom);
        ctx.setLineJoin(StrokeLineJoin.BEVEL);


        //шестиугольник
        ctx.strokePolygon(
                new double[]{0, c, c, 0, -c, -c},
                new double[]{a, a/2, -a/2, -a, -a/2, a/2},
                6
        );

        //ромб
        ctx.strokePolygon(
                new double[]{0, c, 0, -c},
                new double[]{a, 0, -a, 0},
                4
        );
        ctx.setFill(Color.CYAN);
        ctx.fillPolygon(
                new double[]{0, c, 0, -c},
                new double[]{a, 0, -a, 0},
                4
        );

        //круг
        ctx.strokeOval(
                -r,
                r -d,
                2 * r,
                2 * r
        );
        ctx.setFill(Color.WHITE);
        ctx.fillOval(
                -r,
                r -d,
                2 * r,
                2 * r
        );

        // востановление состояния матрицы
        ctx.restore();
    }

    // движение фигуры с помощью мыши
    public void onMouseDragged(MouseEvent mouseEvent)
    {
        offsetX += mouseEvent.getX() - pressedX;
        offsetY += mouseEvent.getY() - pressedY;
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
        draw();
    }

    double pressedX;
    double pressedY;
    public void onMosePressed(MouseEvent mouseEvent)
    {
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
    }
}
