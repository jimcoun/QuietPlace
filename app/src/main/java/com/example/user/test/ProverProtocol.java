package com.example.user.test;


public class ProverProtocol extends Protocol {

    public ProverProtocol(android.content.Context c, String exactLocation, String protocolConfig){

        super(c, exactLocation, protocolConfig);
    }

    @Override
    public void init() {

    }

    @Override
    public void run() {

        parseJSON parse = new parseJSON();

        ProverRequest pr = new ProverRequest("547a9d0da2bd6977d6e3618fe67c9cb810b5cf4a86d71348000f8278049632de",
                "GAHSKDNAJSK", "547a9d0da2bd6977d6e3618fe67c9cb810b5cf4a86d71348000f8278049632de", 23890, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOWCczm6+z0Pgo9mrnG1j8zFI8EHZNgKqqlcZSfXkTZoOmW5UYzSBSvDKBKfe0jkzZrIReHphndnPEFNX1gPa92MZCKF5yCfvC8y6NVXyBn0kJwjHTVXXaP1oJ7wtM2l6cxY/FJkrOlLwjaYt+lJDiUQY1/ot/l8Xzg1pagKOfrwIDAQAB");
        String proverRequest = parse.toJSON(pr);

        sendMessage(proverRequest);



    }
}
