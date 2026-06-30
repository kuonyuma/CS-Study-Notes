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

// 插入排序：将 src 字符串中的字符按升序排序后存入 dest
void insertSort(char *dest, char *src)
{
    int len = strlen(src);
    if (len == 0)
    {
        dest[0] = '\0';
        return;
    }

    // 先将 src 复制到 dest
    strcpy(dest, src);

    // 插入排序：从第二个字符开始，逐个插入到前面已排序的部分
    for (int i = 1; i < len; i++)
    {
        char key = dest[i];
        int j = i - 1;

        // 将大于 key 的元素向后移动
        while (j >= 0 && dest[j] > key)
        {
            dest[j + 1] = dest[j];
            j--;
        }
        dest[j + 1] = key;
    }
    dest[len] = '\0';
}

int main()
{
    char result[1000]; // 在主函数中开辟空间

    printf("请输入若干个单词（输入 Ctrl+Z 结束）：\n");
    t2(result, sizeof(result));

    printf("拼接后的结果为：%s\n", result);
    return 0;
}
