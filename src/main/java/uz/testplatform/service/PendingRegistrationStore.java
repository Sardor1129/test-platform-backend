package uz.testplatform.service;

import org.springframework.stereotype.Component;
import uz.testplatform.dto.RegisterRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRegistrationStore {
    private final Map <String, RegisterRequest> pending = new ConcurrentHashMap<>();

    public void put(String phone, RegisterRequest request){
        pending.put(phone, request);
    }

    public RegisterRequest get(String phone){
        return pending.get(phone);
    }

    public void remove(String phone){
        pending.remove(phone);
    }

}
