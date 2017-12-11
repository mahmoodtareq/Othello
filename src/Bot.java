import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HP on 08-Dec-17.
 */
public class Bot {
    public static char blank = '-';
    public static int dr[] = {1, -1, 0, 0, 1, 1, -1, -1};
    public static int dc[] = {0, 0, 1, -1, 1, -1, 1, -1};
    public static int dirs = 8;

    public Runtime rt;
    public Process proc;
    public String command = ".\\bot.exe ";
    public static int N = 8;
    public char[][] game = new char[N][];
    public char player;
    public Move lastMove;

    public Bot(char player)
    {
        for(int i = 0; i < N; i++)
        {
            game[i] = new char[N];
            for(int j = 0; j < N; j++)
            {
                game[i][j] = blank;
            }
        }
        game[3][3] = game[4][4] = 'W';
        game[4][3] = game[3][4] = 'B';

        this.player = player;
        this.rt = Runtime.getRuntime();
    }

    public String gameToString()
    {
        String str = "" + player + "\n";
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {
                str = str + game[i][j];
            }
            str = str + "\n";
        }
        return str;
    }

    public Move getMove()
    {
        rt = Runtime.getRuntime();
        Move move = new Move(-1, -1);
        try {
            // save game state to file
            System.out.println(gameToString());
            PrintWriter out = new PrintWriter("in.txt");
            out.println(gameToString());
            out.close();

            proc = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s = null;
            String output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            s = null;
            while ((s = stdError.readLine()) != null) {
            }
            output = output.trim();


            System.out.println(output);
            move.row = output.charAt(0) - '0';
            move.col = output.charAt(2) - '0';

            System.out.println("l");



        } catch (IOException e) {
            e.printStackTrace();
        }
        return move;
    }


    public boolean flip(char player, int r, int c, int rInc, int cInc)
    {
        if(r < 0 || r >= N || c < 0 || c >= N) return false;
        if(game[r][c] == blank) return false;
        if(game[r][c] == player) return true;
        boolean found = flip(player, r + rInc, c + cInc, rInc, cInc);
        if(found)
        {
            game[r][c] = player;
        }
        return found;
    }



    public void doMove(Move mv, char player)
    {
        lastMove = mv;
        if(mv.row < 0 || mv.row >= N || mv.col < 0 || mv.col >= N) return;
        game[mv.row][mv.col] = player;
        for(int i = 0; i < dirs; i++)
        {
            flip(player, mv.row + dr[i], mv.col + dc[i], dr[i], dc[i]);
        }
    }


    void moveSearch(HashSet<Move>mvs, char player, int r, int c, int rInc, int cInc)
    {
        int i = r, j = c;
        while(true)
        {
            if(i < 0 || i >= N || j < 0 || j >= N) return;
            if(game[i][j] == player) return;
            else if(game[i][j] == blank)
            {
                mvs.add(new Move(i, j));
                return;
            }
            i = i + rInc;
            j = j + cInc;
        }
    }


    HashSet<Move> actions(char player)
    {
        HashSet<Move> mvs = new HashSet<>();
        char other = 'W';
        if(player == 'W') other = 'B';

        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {
                if(game[i][j] == player)
                {
                    for(int k = 0; k < dirs; k++)
                    {
                        int r = i + dr[k];
                        int c = j + dc[k];
                        if(r < 0 || r >= N || c < 0 || c >= N) continue;
                        if(game[r][c] != other) continue;
                        moveSearch(mvs, player, r + dr[k], c + dc[k], dr[k], dc[k]);
                    }
                }
            }
        }
        return mvs;
    }


    public void playAI()
    {
        Move mv = getMove();
        System.out.println("PC: " + mv.row + " " + mv.col);
        doMove(mv, player);
    }


    public static void main(String[] args) throws IOException {
        Bot bot = new Bot('B');
        Move mv = bot.getMove();
        System.out.println(mv.row + " " + mv.col);
    }
}
