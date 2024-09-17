package com.puneet.httpstreaming.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puneet.httpstreaming.dtos.ChatRequestDTO;
import com.puneet.httpstreaming.dtos.ChatResponseDTO;
import com.puneet.httpstreaming.dtos.GenerateRequest;
import com.puneet.httpstreaming.dtos.GenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChatController {

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/chat")
    public ResponseEntity<List<ChatResponseDTO>> sendChatRequest(@RequestBody ChatRequestDTO requestDTO) {
        String url = "http://localhost:11434/api/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));

        HttpEntity<ChatRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        List<ChatResponseDTO> responseList = new ArrayList<>();

        try {
            restTemplate.execute(url, HttpMethod.POST, clientHttpRequest -> {
                clientHttpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                clientHttpRequest.getHeaders().setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));
                clientHttpRequest.getBody().write(objectMapper.writeValueAsBytes(requestDTO));
            }, clientHttpResponse -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            log.info("Received line: " + line);
                            ChatResponseDTO responseDTO = objectMapper.readValue(line, ChatResponseDTO.class);
                            responseList.add(responseDTO);
                        }
                    }
                }
                return null;
            });
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/chat/unified")
    public ResponseEntity<ChatResponseDTO> sendChatRequestUnified(@RequestBody ChatRequestDTO requestDTO) {
        String url = "http://localhost:11434/api/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));

        String query = requestDTO.getMessages().get(0).getContent();
        StringBuilder builder = new StringBuilder();
        builder.append("prompt: goal is to classify user action and get the desired intent. The intents along with the " +
                "description are - ORDER_LIST - This intent is used when a user is looking for his order list. Example -" +
                " when a user mentions, show me my orders then this shows he wants to see his orders. Similarly another " +
                "example could be when he simply mentions last orders, here also he is looking for his order listing. " +
                "Next intent is ORDER_DETAILS - This intent is to show details of a given order to the user. Examples " +
                "if a user asks where is my order, then this points to ORDER_DETAILS intent.Another example is when user " +
                "mentions that where is my shipment, since shipment belongs to a particular order hence the user is " +
                "looking for order details hence this also points to ORDER_DETAILS intent. Always return the response in " +
                "following format- INTENT: {}, Reasoning: {}, alternate matching intents: []. query: ");
        builder.append(query);
        requestDTO.getMessages().get(0).setContent(builder.toString());

        HttpEntity<ChatRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        List<ChatResponseDTO> responseList = new ArrayList<>();

        try {
            restTemplate.execute(url, HttpMethod.POST, clientHttpRequest -> {
                clientHttpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                clientHttpRequest.getHeaders().setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));
                clientHttpRequest.getBody().write(objectMapper.writeValueAsBytes(requestDTO));
            }, clientHttpResponse -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            ChatResponseDTO responseDTO = objectMapper.readValue(line, ChatResponseDTO.class);
                            responseList.add(responseDTO);
                        }
                    }
                }
                return null;
            });
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        StringBuilder responseBuilder = new StringBuilder();
        responseList.forEach(chatResponseDTO -> {
            responseBuilder.append(chatResponseDTO.getMessage().getContent());
        });

        ChatResponseDTO chatResponseDTO = responseList.get(responseList.size() - 1);
        chatResponseDTO.getMessage().setContent(responseBuilder.toString());
        return ResponseEntity.ok(chatResponseDTO);
    }

    @PostMapping("/chat/stream")
    public ResponseEntity<StreamingResponseBody> sendChatRequestAsStream(@RequestBody ChatRequestDTO requestDTO) {
        String url = "http://localhost:11434/api/chat";

        StreamingResponseBody stream = out -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));

            String query = requestDTO.getMessages().get(0).getContent();
            StringBuilder builder = new StringBuilder();
            builder.append("prompt: goal is to classify user action and get the desired intent. The intents along with the " +
                    "description are - ORDER_LIST - This intent is used when a user is looking for his order list. Example -" +
                    " when a user mentions, show me my orders then this shows he wants to see his orders. Similarly another " +
                    "example could be when he simply mentions last orders, here also he is looking for his order listing. " +
                    "Next intent is ORDER_DETAILS - This intent is to show details of a given order to the user. Examples " +
                    "if a user asks where is my order, then this points to ORDER_DETAILS intent.Another example is when user " +
                    "mentions that where is my shipment, since shipment belongs to a particular order hence the user is " +
                    "looking for order details hence this also points to ORDER_DETAILS intent. Always return the response in " +
                    "following format- INTENT: {}, Reasoning: {}, alternate matching intents: []. query: ");
            builder.append(query);
            requestDTO.getMessages().get(0).setContent(builder.toString());

            HttpEntity<ChatRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            List<ChatResponseDTO> responseList = new ArrayList<>();

            try {
                restTemplate.execute(url, HttpMethod.POST, clientHttpRequest -> {
                    clientHttpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    clientHttpRequest.getHeaders().setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));
                    clientHttpRequest.getBody().write(objectMapper.writeValueAsBytes(requestDTO));
                }, clientHttpResponse -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) {
                                log.info("Received line: " + line);
                                out.write((line + "\n").getBytes());
                                out.flush();
                            }
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                // Handle exceptions appropriately
                e.printStackTrace();
                out.write(e.getMessage().getBytes());
                out.flush();
            }

        };


        return new ResponseEntity(stream, HttpStatus.OK);
    }

    @PostMapping("/chat/generate")
    public ResponseEntity<GenerateResponse> generateResponse(@RequestBody GenerateRequest requestDTO) {
        String url = "http://localhost:11434/api/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));

        HttpEntity<GenerateRequest> requestEntity = new HttpEntity<>(requestDTO, headers);

        List<GenerateResponse> responseList = new ArrayList<>();

        try {
            restTemplate.execute(url, HttpMethod.POST, clientHttpRequest -> {
                clientHttpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                clientHttpRequest.getHeaders().setAccept(List.of(MediaType.parseMediaType("application/x-ndjson")));
                clientHttpRequest.getBody().write(objectMapper.writeValueAsBytes(requestDTO));
            }, clientHttpResponse -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            GenerateResponse responseDTO = objectMapper.readValue(line, GenerateResponse.class);
                            responseList.add(responseDTO);
                        }
                    }
                }
                return null;
            });
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        StringBuilder responseBuilder = new StringBuilder();
        responseList.forEach(chatResponseDTO -> {
            responseBuilder.append(chatResponseDTO.getResponse());
        });

        GenerateResponse chatResponseDTO = responseList.get(responseList.size() - 1);
        chatResponseDTO.setResponse(responseBuilder.toString());
        return ResponseEntity.ok(chatResponseDTO);
    }
}
