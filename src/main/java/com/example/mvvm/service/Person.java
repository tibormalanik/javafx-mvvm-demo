package com.example.mvvm.service;

import java.util.UUID;

public record Person(UUID uid, String firstName, String lastName, String email) {}
