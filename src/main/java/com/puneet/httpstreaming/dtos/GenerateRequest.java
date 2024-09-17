package com.puneet.httpstreaming.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GenerateRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("suffix")
    private String suffix;

    @JsonProperty("system")
    private String system;

    @JsonProperty("template")
    private String template;

    @JsonProperty("context")
    private List<Integer> context;

    @JsonProperty("stream")
    private Boolean stream;

    @JsonProperty("raw")
    private boolean raw;

    @JsonProperty("format")
    private String format;

    @JsonProperty("options")
    private Map<String, Object> options;

    public GenerateRequest() {
    }

    public GenerateRequest(String model, String prompt, String suffix, String system, String template, List<Integer> context, Boolean stream, boolean raw, String format) {
        this.model = model;
        this.prompt = prompt;
        this.suffix = suffix;
        this.system = system;
        this.template = template;
        this.context = context;
        this.stream = stream;
        this.raw = raw;
        this.format = format;
    }

    // Getters and setters omitted for brevity

}