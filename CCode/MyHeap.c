#include <stdio.h>
#include <stdlib.h>

typedef struct {
     int size;
     int capacity;
     int* data;
} Heap;

void swap(int* num1, int* num2);
void upCapacity(Heap* heap);
int insert(int value, Heap* heap);
int peek(Heap* heap);
int pop(Heap* heap);
void up(Heap* heap);
void shift(Heap* heap);


//基本的交换函数
void swap(int* num1, int* num2){
    int tmp = *num1;
    *num1 = *num2;
    *num2 = tmp;
}

//扩容函数
void upCapacity(Heap* heap){
    int newCapacity = heap->capacity > 0 ? heap->capacity * 2 : 1;
    int* newData = realloc(heap->data, newCapacity * sizeof(int));
    //扩容失败
    if(newData == NULL){
        printf("日志: 扩容失败");
    } else {
        heap->data = newData;
        heap->capacity = newCapacity;
    }
}

//尾插:返回数组长度
int insert(int value, Heap* heap){
    //容量满了触发扩容
    if(heap->capacity == heap->size){
        upCapacity(heap);
        if(heap->capacity == heap->size){
            return -1;
        }
    }

    heap->data[heap->size] = value;
    heap->size++;

    //插入后向上调整
    up(heap);
    return heap->size - 1;
}

//获取堆顶元素
int peek(Heap* heap){
    if(heap->size <= 0){
        printf("堆为空\n");
        return -1;
    }
    return heap->data[0];
}

//弹出堆顶元素
int pop(Heap* heap){
    if(heap->size <= 0){
        printf("堆为空\n");
        return -1;
    }
    int val = heap->data[0];
    heap->data[0] = heap->data[heap->size - 1];
    heap->size--;
    if(heap->size > 0){
        shift(heap);
    }
    return val;
}

//向上调整
void up(Heap* heap){
    int* cur = heap->data;
    int index = heap->size - 1;
    while(index > 0){
        //计算父亲的位置
        int parentIndex = (index - 1) / 2;

        //子节点与父节点比大小
        if(*(cur+index) >  *(cur + parentIndex)){
            swap((cur+index),(cur + parentIndex));
        }else{
            break;
        }
        index = parentIndex;
    }

}
    
//向下调整

void shift(Heap* heap){
    int* a = heap->data;
    int size = heap->size;
    int index = 0;

    while (1) {
        int left = index * 2 + 1;
        int right = left + 1;
        int cur = index;

        if (left < size && a[left] > a[cur]) {
            cur = left;
        }
        if (right < size && a[right] > a[cur]) {
            cur = right;
        }
        if (cur == index) {
            break;
        }

        swap(&a[index], &a[cur]);
        index = cur;
    }
}

// 用C语言实现堆一个基于数组的heap
int main(){

    printf("程序运行中..\n");
    printf("请输入大小:...");

    Heap heap;
    scanf("%d",&heap.capacity);
    heap.size = 0;
    //实例化一个数组
    heap.data = (int*)malloc(heap.capacity * sizeof(int));
    if(heap.data == NULL){
        printf("日志: 初始化失败\n");
        return 1;
    }

    // 简单测试
    insert(3, &heap);
    insert(10, &heap);
    insert(5, &heap);
    insert(8, &heap);
    insert(1, &heap);

    printf("堆顶元素: %d\n", peek(&heap));

    printf("依次弹出: ");
    while(heap.size > 0){
        printf("%d ", pop(&heap));
    }
    printf("\n");

    free(heap.data);
    heap.data = NULL;


    return 0;
}

