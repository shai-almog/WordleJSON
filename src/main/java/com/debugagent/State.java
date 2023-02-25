package com.debugagent;

import java.util.List;

public record State(String word, long time, List<String> attempts) {
}
