#include <stdio.h>
#include <string.h>

void put1()
{

    int a, b;
    printf("请输入多组数据\n");
    while (scanf("%d %d", &a, &b) != EOF)
    {
        printf("%d,%d\n", a, b);
    }
    printf("程序结束\n");
}

void put2()
{
    int t;
    if (scanf("%d", &t) == 1)
    {
        while (t > 0)
        {
            int a, b;
            scanf("%d %d", &a, &b);
            printf("%d %d\n", a, b);
            t--;
        }
    }
    printf("程序结束\n");
}

void put3()
{

    int n;
    while (scanf("%d", &n) == 1 && n != 0)
    {

        printf("%d\n", n);
    }
}

void t1()
{
    char s1[] = "asdasdd";
    int length = strlen(s1);
    printf("%d\n", length);
}

void t2()
{
    char s[] = "adcdef";
    int length = sizeof(s);
    printf("%d\n", length);
}
void t3()
{
    char s[] = "abcd";
    char s1[] = "abcd";
    int result = strcmp(s, s1);
    printf("%d", result);
}
void t4()
{
    char s[] = "abcd";
    char s1[100];
    strcpy(s1, s);
    printf("%s", s1);
}

void t5()
{
    int i = 0;
    char ch;
    char str[100];
    while ((ch = getchar()) != '\n' && ch != EOF)
    {
        str[i++] = ch;
    }
    str[i] == '\0';
    printf("%s\n", str);
}

void t6()
{
    int i = 0;
    int arr[100];
    int buf;
    char ch;
    while (scanf("%d", &buf) == 1)
    {
        arr[i++] = buf;
        ch = getchar();
        if (ch == '\n')
        {
            printf("输入结束\n");
            break;
        }
    }
    int j = 0;
    while (j < i)
    {
        printf("%d\n", arr[j++]);
    }
}

void t7()
{
    int row, col;
    scanf("%d %d", &row, &col);
    int arr[row][col];

    for (int i = 0; i < row; i++)
    {
        for (int j = 0; j < col; j++)
        {
            scanf("%d", &arr[i][j]);
        }
    }

    for (int i = 0; i < row; i++)
    {
        for (int j = 0; j < col; j++)
        {
            printf("%d ", arr[i][j]);
        }
        printf("/n");
    }
}
int main()
{
    // printf("请开始输入\n");
    // put3();
    t7();
    return 0;
}