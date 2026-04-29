package com.lyuke.mybooksystem.demo2;

public class Myfeind  {

    public int find(byte[] father,int faherLength,
                    byte[]Sum,int SumLength){
        byte first = Sum[0];
        int max = faherLength - SumLength;
        for(int i = 0; i <= max; i++) {
            if (father[i] != first){
                while (++i < max && father[i] != first) ;
            }

            if(i <= max){
                int j = i + 1;
                int end = j + SumLength - 1;
                for(int k = 1;father[j] == Sum[k] && j <= end;j++,k++);

                if(j == end){
                    return i;
                }
            }
        }
        return -1;
    }
}
