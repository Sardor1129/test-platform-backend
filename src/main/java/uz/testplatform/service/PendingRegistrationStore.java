package uz.testplatform.service;

import org.springframework.stereotype.Component;
import uz.testplatform.dto.RegisterRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRegistrationStore {
    private final Map <String, RegisterRequest> pending = new ConcurrentHashMap<>();

    public void put(String email, RegisterRequest request){
        pending.put(email, request);
    }

    public RegisterRequest get(String email){
        return pending.get(email);
    }

    public void remove(String email){
        pending.remove(email);
    }

}
