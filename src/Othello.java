import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;

/**
 * Created by HP on 08-Dec-17.
 */
public class Othello extends Application{
    public static int BUTTON_PADDING = 0;
    public static int N = 8;

    public static char human = 'W';
    public static char ai = 'B';

    Label lblHuman;
    Label lblAI;


    public void changeScene(Button btns[][], Bot bot, char player)
    {
        int humanCnt = 0, aiCnt = 0;
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {
                if(i == bot.lastMove.row && j == bot.lastMove.col)
                {
                    btns[i][j].setStyle("-fx-border-color: gold; -fx-background-color: lightgreen");
                }
                else
                {
                    btns[i][j].setStyle("-fx-border-color: black; -fx-background-color: #009933;");
                }


                if(bot.game[i][j] == 'B')
                {
                    //btns[i][j].setStyle("-fx-border-color: lightblue; -fx-background-color: black");
                    btns[i][j].setGraphic(new Circle(15, Color.BLACK));
                }

                else if(bot.game[i][j] == 'W')
                {
                    btns[i][j].setGraphic(new Circle(15, Color.WHITE));
                    //btns[i][j].setStyle("-fx-border-color: lightblue; -fx-background-color: white");
                }
                else
                {
                    btns[i][j].setGraphic(null);
                }

                if(bot.game[i][j] == human) humanCnt++;
                else if(bot.game[i][j] == ai) aiCnt++;

                lblHuman.setText("Human: " + humanCnt);
                lblAI.setText("AI: " + aiCnt);

                //btns[i][j].setDisable(true);
            }
        }
        HashSet<Move> mvs = bot.actions(player);
        Iterator<Move> it = mvs.iterator();
        while(it.hasNext())
        {
            Move mv = it.next();
            //btns[mv.row][mv.col].setDisable(false);
            btns[mv.row][mv.col].setGraphic(new Circle(5, Color.DARKGREEN));
        }

        Move mv = bot.lastMove;
    }


    public void start(Stage stage) {
        Button btns[][] = new Button[N][];
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(BUTTON_PADDING));
        grid.setHgap(BUTTON_PADDING);
        grid.setVgap(BUTTON_PADDING);

        VBox vBox = new VBox();
        vBox.setPrefWidth(50);
        vBox.setPrefHeight(50);

        Bot bot = new Bot('B');


        EventHandler<MouseEvent> event = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle( final MouseEvent ME )
            {
                Object obj = ME.getSource();
                if (obj instanceof Button)
                {
                    Button btn = (Button) obj;
                    Move mv = (Move) btn.getUserData();

                    System.out.println("PL: " + mv.row + " " + mv.col);

                    HashSet<Move> mvs = bot.actions('W');
                    if(!mvs.contains(mv))
                    {
                        System.out.println("Invalid");
                        return;
                    }

                    bot.doMove(mv, 'W');

                    changeScene(btns, bot, 'B');

                    Timer timer = new Timer();
                    timer.schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    bot.playAI();
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            changeScene(btns, bot, 'W');
                                        }
                                    });
                                }
                            },
                            2000
                    );
                }
            }
        };


        for (int r = 0; r < N; r++) {
            btns[r] = new Button[N];
            for (int c = 0; c < N; c++) {
                Button button = new Button();
                btns[r][c] = button;
                button.setMinWidth(vBox.getPrefWidth());
                button.setMinHeight(vBox.getPrefHeight());
                button.setStyle("-fx-border-color: black; -fx-background-color: #009933;");
                button.setUserData(new Move(r, c));
                button.setOnMouseClicked(event);
                grid.add(button, c, r);
            }
        }

        lblHuman = new Label("Human: 2");
        lblHuman.setMinHeight(10);
        lblHuman.setMinWidth(100);
        lblHuman.setStyle("-fx-alignment: center");
        grid.add(lblHuman, 8, 3);

        lblAI = new Label("AI: 2");
        lblAI.setMinHeight(10);
        lblAI.setMinWidth(100);
        lblAI.setStyle("-fx-alignment: center");
        grid.add(lblAI, 8, 4);

        ScrollPane scrollPane = new ScrollPane(grid);

        stage.setScene(new Scene(scrollPane));
        stage.setTitle("Othello");
        stage.show();

        bot.playAI();
        changeScene(btns, bot, 'W');
    }

    @Override
    public void stop()
    {
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
