package org.Garochior.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;

import java.util.ArrayList;
import java.util.List;

public final class NetworkMessage {

    //CONSTRUIRE MESAJE

    public static String setHand(int playerId, List<Card> cards) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.SET_HAND);
        obj.addProperty("playerId", playerId);
        JsonArray arr = new JsonArray();
        for (Card card : cards) {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("number", card.getNumber());
            cardObj.addProperty("cardType", card.getType().name());
            arr.add(cardObj);
        }
        obj.add("cards", arr);
        return obj.toString();
    }

    public static String yourTurn(int playerId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.YOUR_TURN);
        obj.addProperty("playerId", playerId);
        return obj.toString();
    }

    /// Asta este trimis de client
    public static String cardSelected(int playerId, Card card) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.CARD_SELECTED);
        obj.addProperty("playerId", playerId);
        obj.addProperty("number", card.getNumber());
        obj.addProperty("cardType", card.getType().name());
        return obj.toString();
    }

    public static String cardPlayed(int playerId, Card card) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.CARD_PLAYED);
        obj.addProperty("playerId", playerId);
        obj.addProperty("number", card.getNumber());
        obj.addProperty("cardType", card.getType().name());
        return obj.toString();
    }

    ///Trimis de server catre clientul care a ales cartea gresita
    public static String invalidCard(int playerId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.INVALID_CARD);
        obj.addProperty("playerId", playerId);
        return obj.toString();
    }

    public static String handTaker(int playerId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.HAND_TAKER);
        obj.addProperty("playerId", playerId);
        return obj.toString();
    }

    public static String gameStart(String gameName) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.GAME_START);
        obj.addProperty("gameName", gameName);
        return obj.toString();
    }

    public static String gameEnd(List<Integer> scores) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.GAME_END);
        JsonArray arr = new JsonArray();
        for (int score : scores) arr.add(score);
        obj.add("scores", arr);
        return obj.toString();
    }


    public static String roomReady(int playerId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", MessageType.ROOM_READY);
        obj.addProperty("playerId", playerId);
        return obj.toString();
    }

    //PARSARE MESAJE

    public static JsonObject parse(String message) {
        return JsonParser.parseString(message).getAsJsonObject();
    }

    public static String getType(JsonObject obj) {
        return obj.get("type").getAsString();
    }

    public static int getPlayerId(JsonObject obj) {
        return obj.get("playerId").getAsInt();
    }

    public static int getCardIndex(JsonObject obj) {
        return obj.get("cardIndex").getAsInt();
    }

    public static String getGameName(JsonObject obj) {
        return obj.get("gameName").getAsString();
    }

    public static String getRoomPlayers (JsonObject obj) {
        return obj.get("players").getAsString();
    }

    public static Card getCard(JsonObject obj) {
        int number = obj.get("number").getAsInt();
        CardType type = CardType.valueOf(obj.get("cardType").getAsString());
        return new Card(number, type);
    }


    public static List<Card> getCards(JsonObject obj) {
        List<Card> cards = new ArrayList<>();
        JsonArray arr = obj.getAsJsonArray("cards");
        for (int i = 0; i < arr.size(); i++) {
            JsonObject cardObj = arr.get(i).getAsJsonObject();
            int number = cardObj.get("number").getAsInt();
            CardType type = CardType.valueOf(cardObj.get("cardType").getAsString());
            cards.add(new Card(number, type));
        }
        return cards;
    }

    public static List<Integer> getScores(JsonObject obj) {
        List<Integer> scores = new ArrayList<>();
        JsonArray arr = obj.getAsJsonArray("scores");
        for (int i = 0; i < arr.size(); i++) {
            scores.add(arr.get(i).getAsInt());
        }
        return scores;
    }
}