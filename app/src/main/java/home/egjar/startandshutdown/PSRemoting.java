package home.egjar.startandshutdown;

import io.cloudsoft.winrm4j.client.WinRmClientContext;
import io.cloudsoft.winrm4j.winrm.WinRmTool;
import org.apache.http.client.config.AuthSchemes;

class PSRemoting {
    private WinRmTool connection;
    private WinRmClientContext context;
    PSRemoting(String address, String domain, String username, String password) {
        context = WinRmClientContext.newInstance();
        connection = WinRmTool.Builder.builder(address,domain,username,password)
                .authenticationScheme(AuthSchemes.NTLM)
                .port(5985)
                .useHttps(false)
                .context(context)
                .build();
    }
    public void finalize() {
        context.shutdown();
    }
    void remoteShutdown() {
        connection.executePs("Stop-Computer");
        context.shutdown();
    }
    void remoteSuspend() {
        connection.executeCommand("Rundll32.exe Powrprof.dll,SetSuspendState");
        context.shutdown();
    }

    void remoteExecuteCustomCommand(String command) {
        connection.executeCommand(command);
        context.shutdown();
    }
}
