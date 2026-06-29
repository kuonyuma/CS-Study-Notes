#include <stdio.h>

#include <String.h>

// 多组测试数据输入
void t1()
{
    int n, m;
    int tmp;
    while ((tmp = scanf("%d %d", &n, &m)) == 2 && tmp != 0)
    {
        printf("%d %d\n", n, m);
    }
}

// 拼接字符串
char *t2(char *ret, int maxLen)
{
    char base[100];
    ret[0] = '\0';
    while (scanf("%s", base) != EOF)
    {
        if (strcmp(base, "end") == 0)
            break;
        if (strlen(ret) + strlen(base) < maxLen - 1)
        {
            strcat(ret, base);
        }
        else
        {
            printf("缓存大小不足");
            break;
        }
    }
}

int main()
{
    char result[1000]; // 在主函数中开辟空间

    printf("请输入若干个单词（输入 Ctrl+Z 结束）：\n");
    t2(result, sizeof(result));

    printf("拼接后的结果为：%s\n", result);
    return 0;
}
