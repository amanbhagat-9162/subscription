package org.example.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.example.subscription.entity.AddOn;
import org.example.subscription.service.AddOnService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class AddOnController {

    private final AddOnService addOnService;

    @PostMapping
    public AddOn createAddOn(@RequestBody AddOn addOn) {
        return addOnService.createAddOn(addOn);
    }

    @PostMapping("/{subscriptionId}/{addOnId}")
    public String attachAddOn(@PathVariable Long subscriptionId,
                              @PathVariable Long addOnId) {

        addOnService.attachAddOn(subscriptionId, addOnId);
        return "AddOn attached successfully";
    }

    @DeleteMapping("/{subscriptionId}/{addOnId}")
    public String removeAddOn(@PathVariable Long subscriptionId,
                              @PathVariable Long addOnId) {

        addOnService.removeAddOn(subscriptionId, addOnId);
        return "AddOn removed successfully";
    }
}
