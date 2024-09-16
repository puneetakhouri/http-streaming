package com.puneet.httpstreaming;

import com.fasterxml.jackson.databind.ObjectMapper;
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
}
