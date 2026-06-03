#include<stdio.h>

//升序
void bubbleSort(int* p ,int len){
    for(int i = 0;i < len - 1;i++){

        for(int j = 0;j < len - i - 1;j++){

            if(p[j] > p[j + 1]){

                int temp = p[j];
                p[j] = p[j + 1];
                p[j + 1] = temp;
            }
        }
    }
}

int main(){
    //定义行与列
    int row;
    scanf("%d", &row);
    int col;
    scanf("%d", &col);

    //定义二维数组
    int arr[row][col];

    //输入二维数组
    for(int i = 0; i < row; i++){
        for(int j = 0; j < col; j++){
            scanf("%d", &arr[i][j]);
        }
    }

    int midSum = 0;
    for(int i = 0;i < row;i++){
        int tmp[col];
        for(int j = 0;j < col;j++){
            //将每一行的元素赋值给tmp
           tmp[j] = arr[i][j];
        }
        //对这个临时数组排序
        bubbleSort(tmp, col);
        //找到这个临时数组的中位数
        if(col % 2 == 0){
            midSum += tmp[col / 2 -1]+ tmp[col / 2];
        }else{
            midSum += tmp[col / 2];
        }
    }

    printf("%d", midSum);
    printf("\n");
    return 0;
}