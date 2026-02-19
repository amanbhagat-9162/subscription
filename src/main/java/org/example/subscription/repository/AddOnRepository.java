package org.example.subscription.repository;

import org.example.subscription.entity.AddOn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddOnRepository extends JpaRepository<AddOn, Long> {
}
