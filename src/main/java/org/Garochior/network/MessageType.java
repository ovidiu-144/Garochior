package org.Garochior.network;

public class MessageType {
    //SET_HAND, YOUR_TURN, PLAY_CARD, CARD_PLAYED, HAND_WINNER, GAME_START, GAME_OVER

    public static final String SET_HAND = "SET_HAND";
    public static final String YOUR_TURN = "YOUR_TURN";

    //clientul alege cartea, si o sa avem card_selected iar serverul trimite card_played catre toti ceilalti daca este buna, daca nu, trimite invalid_card catre clientul care a ales cartea
    public static final String CARD_SELECTED = "CARD_SELECTED";
    public static final String CARD_PLAYED = "CARD_PLAYED";
    public static final String INVALID_CARD = "INVALID_CARD";

    public static final String HAND_TAKER = "HAND_TAKER";
    public static final String TABLOU_GAME = "TABLOU_GAME";

    public static final String GAME_START = "GAME_START";
    public static final String GAME_END = "GAME_END";
    public static final String GAME_OVER = "GAME_OVER";  //cand se termina un ciclu de 4 jocuri

    //pentru deconectarea cuiva
    public static final String HOST_DISCONNECTED = "HOST_DISCONNECTED";
    public static final String CLIENT_DISCONNECTED = "CLIENT_DISCONNECTED";

    public static final String ROOM_FOUND = "ROOM_FOUND";
    public static final String ROOM_NOT_FOUND = "ROOM_NOT_FOUND";

    //pentru conectarea clientilor
    public static final String ROOM_READY = "ROOM_READY";

//    public static final int NUMBER_OF_PLAYERS = 0;
}
