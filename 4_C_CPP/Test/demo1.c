#include <stdio.h>
#include <string.h>

char *subString(char *left, char *right, char *head, int size)
{
    while (*left == 'x' && left <= right)
        left++;
    while (*right == 'x' && left <= right)
        right--;
    // 复制字符串
    int i = 0;
    while (left <= right)
        head[i++] = *left++;
    head[i] = '\0';
    return head;
}

int main()
{
    char str[] = "xxxxxadadaxxxxx";
    int len = strlen(str);
    char result[len];
    subString(&str[0], &str[len - 1], &result[0], len);

    printf("%s", result);
    return 0;
}