package com.sushkov.projects.quoine;


import com.sushkov.projects.quoine.service.CurPairRequestService;

public class Main {


    public static void main(String[] args) {

        CurPairRequestService requestService = new CurPairRequestService();
        requestService.startCurPairRequestService();
    }

}
