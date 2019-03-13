package com.template.webserver;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.template.VideoState;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/peers", produces = "text/plain")
    private String peers() {
        return proxy.networkMapSnapshot().toString();
    }
    
    @GetMapping(value = "/me", produces = "text/plain")
    private String me() {
        return proxy.nodeInfo().getLegalIdentities().get(0).toString();
    }
    
    @GetMapping(value = "infojson", produces = "text/plain")
    private String infojson() {
        List<StateAndRef<VideoState>> states = proxy.vaultQuery(VideoState.class).getStates();
        List<HashMap<String, String>> info = states.stream().map(stateAndRef -> {
            VideoState Video = stateAndRef.getState().getData();

            HashMap<String, String> map = new HashMap<>();
            map.put("Band_Name", Video.getIdentifier());
            map.put("Score", String.valueOf(Video.getScore()));

            return map;
        }).collect(Collectors.toList());
        String end = "[\n\t";
        if (info.size() > 0) {
        		for (int x = 0; x < info.size()-1; x++) {
        			end += "{\n\t\t\"Band_Name\": \"" + info.get(x).get("Band Name") + "\",\n";
      	 		end += "\t\t\"Score\": " + info.get(x).get("Score") + "\n\t},";
        		}
        		end += "\n\t{\n\t\t\"Band_Name\": \"" + info.get(info.size()-1).get("Band Name") + "\",\n";
      	 	end += "\t\t\"Score\": " + info.get(info.size()-1).get("Score") + "\n\t}";
        }
        end+= "\n]";
        return end;
    }
}