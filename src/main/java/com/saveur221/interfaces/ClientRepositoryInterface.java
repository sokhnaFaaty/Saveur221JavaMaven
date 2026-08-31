package com.saveur221.interfaces;

import java.util.Optional;

import com.saveur221.entities.Client;

public interface ClientRepositoryInterface {
    Optional<Client> findById(Long id);

}
