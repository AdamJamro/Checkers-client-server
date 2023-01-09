package com.example.demo.CheckersCommunicationTessts;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CommunicationTestRussianCheckers extends CommunicationTest{

    @Test
    public void CommunicationTestForRussianCheckers() throws IOException, InterruptedException {
        playGame(initializeGame("RUSSIAN"));
    }

}
