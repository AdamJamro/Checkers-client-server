package com.example.demo.CheckersCommunicationTessts;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CommunicationTestClassicCheckers extends CommunicationTest{

    @Test
    public void CommunicationTestForClassicCheckers() throws IOException, InterruptedException {
        playGame(initializeGame("CLASSIC"));
    }


}
