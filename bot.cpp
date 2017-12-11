#include <bits/stdc++.h>
#include <windows.h>

#define sd(x)       scanf("%d", &x)
#define sdn(x)      scanf("%d\n", &x)
#define slld(x)     scanf("%I64d", &x)
#define sc(x)       scanf("%c", &x)
#define pd(x)       printf("%d", x)
#define plld(x)     printf("%I64d\n", x)
#define pc(x)       printf("%c", x)
#define pdln(x)     printf("%d\n", x)
#define prl(x)      cout << x << endl
#define LL          long long
#define forn(i, n)  for(int i = 0; i < (int)n; i++)
#define fori(i, n)  for(int i = 1; i <= (int)n; i++)
#define revn(i, n)  for(int i = (int)(n - 1); i >= 0; i--)
#define pb          push_back
#define pii         pair <int, int>

#define MAX_DEPTH   12
#define INF         100
#define NOMOVE      50
#define N 8

using namespace std;

char white = 'W', black = 'B';
char mayPlay = '+', blank = ' ';
char human = black, pc = white;
int turn = 0;
int node_cnt = 0;

int dr[] = {1, -1, 0, 0, 1, 1, -1, -1};
int dc[] = {0, 0, 1, -1, 1, -1, 1, -1};
int dirs = 8;

struct Move
{
    int row, col;
    Move(int r, int c)
    {
        row = r; col = c;
    }

    bool operator<(const Move& m) const
    {
        if(row != m.row) return row < m.row;
        return col < m.col;
    }
};


struct State
{
    char grid[N][N];

    State()
    {
        forn(i, N) forn(j, N) grid[i][j] = blank;
    }

    void show()
    {
        printf("\n*  ");
        forn(i, N)
        {
            printf("%d ", i + 1);
        }
        printf("\n");
        forn(i, N)
        {
            printf("%d  ", i + 1);
            forn(j, N)
            {
                printf("%c ", grid[i][j]);
            }
            printf("\n");
        }
        printf("\n");
    }
};


State init()
{
    State state;
    state.grid[3][3] = state.grid[4][4] = white;
    state.grid[3][4] = state.grid[4][3] = black;
    return state;
}


bool flip(State* state, char player, int r, int c, int rInc, int cInc)
{
    if(r < 0 || r >= N || c < 0 || c >= N) return false;
    if(state->grid[r][c] == blank) return false;
    if(state->grid[r][c] == player) return true;
    bool found = flip(state, player, r + rInc, c + cInc, rInc, cInc);
    if(found)
    {
        state->grid[r][c] = player;
    }
    return found;
}


State result(State state, Move mv, char player)
{
    state.grid[mv.row][mv.col] = player;
    forn(i, dirs)
    {
        flip(&state, player, mv.row + dr[i], mv.col + dc[i], dr[i], dc[i]);
    }
    return state;
}

int utility(State* state)
{
    int pcCnt = 0;
    int humanCnt = 0;
    forn(i, N)
    {
        forn(j, N)
        {
            if(state->grid[i][j] == pc) pcCnt++;
            else if(state->grid[i][j] == human) humanCnt++;
        }
    }
    return pcCnt - humanCnt;
}

bool isThereGap(State* state)
{
    forn(i, N)
    {
        forn(j, N)
        {
            if(state->grid[i][j] == blank) return true;
        }
    }
    return false;
}


bool noPlayerPiece(State* state, char player)
{

    forn(i, N)
    {
        forn(j, N)
        {
            if(state->grid[i][j] == player) return false;
        }
    }
    return true;
}

char winner(State* state)
{
    int util = utility(state);
    if(util > 0) return pc;
    else if(util < 0) return human;
    else return blank;
}



char nextPlayer(char player)
{
    if(player == black) return white;
    return black;
}


void moveSearch(State* state, set<Move>& mvs, char player, int r, int c, int rInc, int cInc)
{
    int i = r, j = c;
    while(1)
    {
        if(i < 0 || i >= N || j < 0 || j >= N) return;
        if(state->grid[i][j] == player) return;
        else if(state->grid[i][j] == blank)
        {
            mvs.insert(Move(i, j));
            return;
        }
        i = i + rInc;
        j = j + cInc;
    }
}

set<Move> actions(State* state, char player)
{
    set<Move> mvs;
    char other = nextPlayer(player);
    forn(i, N)
    {
        forn(j, N)
        {
            if(state->grid[i][j] == player)
            {
                forn(k, dirs)
                {
                    int r = i + dr[k];
                    int c = j + dc[k];
                    if(r < 0 || r >= N || c < 0 || c >= N) continue;
                    if(state->grid[r][c] != other) continue;
                    moveSearch(state, mvs, player, r + dr[k], c + dc[k], dr[k], dc[k]);
                }
            }
        }
    }
    return mvs;
}


bool terminalTest(State* state, int depth)
{
    if(depth >= MAX_DEPTH) return true;
    forn(i, N)
    {
        forn(j, N)
        {
            if(state->grid[i][j] == blank) return false;
        }
    }
    return true;
}


void showMoveOptions(State state, char player)
{
    set<Move> mvs = actions(&state, player);
    set<Move>::iterator it = mvs.begin();
    while(it != mvs.end())
    {
        state.grid[it->row][it->col] = mayPlay;
        ++it;
    }
    state.show();
}



float min_value(State, char, int, float, float);
float max_value(State, char, int, float, float);


float max_value(State state, char player, int depth, float alpha, float beta)
{
    node_cnt++;
    //printf("Node %d\n", node_cnt);
    if(terminalTest(&state, depth))
        return utility(&state);

    if(noPlayerPiece(&state, player)) return -INF;

    set<Move> mvs = actions(&state, player);

    if(mvs.size() == 0) return -50;

    set<Move>::iterator it = mvs.begin();
    float maxval = -100;
    while(it != mvs.end())
    {
        float val = min_value(result(state, *it, player), nextPlayer(player), depth + 1, alpha, beta);
        maxval = max(val, maxval);
        if(maxval >= beta)
            return maxval;
        alpha = max(alpha, maxval);
        ++it;
    }
    //forn(i, depth) printf(">>");
    //printf("maxval: %lf\n", maxval);
    return maxval;
}


float min_value(State state, char player, int depth, float alpha, float beta)
{
    node_cnt++;
    //printf("Node %d\n", node_cnt);
    if(terminalTest(&state, depth))
        return utility(&state);

    if(noPlayerPiece(&state, player)) return INF;

    set<Move> mvs = actions(&state, player);

    if(mvs.size() == 0) return 50;

    set<Move>::iterator it = mvs.begin();
    float minval = 100;

    while(it != mvs.end())
    {
        float val = max_value(result(state, *it, player), nextPlayer(player), depth + 1, alpha, beta);
        minval = min(val, minval);
        if(minval <= alpha)
            return minval;
        beta = min(beta, minval);
        ++it;
    }
    //forn(i, depth) printf(">>");
    //printf("minval: %lf\n", minval);
    return minval;
}


Move minimax(State state, char player, float alpha, float beta)
{
    node_cnt++;
    //printf("Node %d\n", node_cnt);
    set<Move> mvs = actions(&state, player);
    set<Move>::iterator it = mvs.begin();
    float maxval = -100.0;
    Move maxMove(-1, -1);
    while(it != mvs.end())
    {
        float val = min_value(result(state, *it, player), nextPlayer(player), 1, alpha, beta);
        if(val > maxval)
        {
            maxval = val;
            maxMove = *it;
        }
        if(maxval >= beta) return *it;
        alpha = max(alpha, maxval);
        ++it;
    }
    return maxMove;
}


int main()
{
    freopen("in.txt", "r", stdin);
    int x, y;
    human = black; pc = white;

    char ch;
    scanf("%c\n", &ch);
    pc = ch;
    State game;
    forn(i, N)
    {
        forn(j, N)
        {
            scanf("%c", &ch);
            if(ch == '-') continue;
            game.grid[i][j] = ch;
        }
        scanf("%c", &ch);
    }
    Move mv = minimax(game, pc, -100, 100);
    printf("%d %d", mv.row, mv.col);
    return 0;
}

