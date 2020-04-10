package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiverTest {

    private ReceiverConfig config;

    ReceiverTest() {
        this.config = new ReceiverConfig.Builder(44100,
                                            ChannelSet.STEREO,
                                            FrameEncoding.PCM_FLOAT)
                                        .build();
    }

    @Test
    public void TestValidReceiverCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            try (
                    Context context = new Context();
                    Receiver receiver = new Receiver(context, config);
            ) {}
        });
    }

    @Test
    public void TestInvalidReceiverCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Receiver(null, config));
        assertThrows(IllegalArgumentException.class, () -> {
            try (Context context = new Context()) { new Receiver(context, null); }
        });
    }

    @Test
    public void TestValidReceiverBind() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            assertDoesNotThrow(() -> receiver.bind(PortType.AUDIO_SOURCE, Protocol.RTP_RS8M_SOURCE, new Address(Family.AUTO, "0.0.0.0", 10001)));
            assertDoesNotThrow(() -> receiver.bind(PortType.AUDIO_REPAIR, Protocol.RS8M_REPAIR, new Address(Family.AUTO, "0.0.0.0", 10002)));
        }
    }

    @Test
    public void TestReceiverBindEphemeralPort() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            Address sourceAddress = new Address(Family.AUTO, "0.0.0.0", 0);
            Address repairAddress = new Address(Family.AUTO, "0.0.0.0", 0);
            receiver.bind(PortType.AUDIO_SOURCE, Protocol.RTP_RS8M_SOURCE, sourceAddress);
            receiver.bind(PortType.AUDIO_REPAIR, Protocol.RS8M_REPAIR, repairAddress);
            assertNotEquals(0, sourceAddress.getPort());
            assertNotEquals(0, repairAddress.getPort());
        }
    }

    @Test
    public void TestInvalidReceiverBind() throws Exception {
        try (
                    Context context = new Context();
                    Receiver receiver = new Receiver(context, config);
        ) {
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(null, Protocol.RTP, new Address(Family.AUTO, "0.0.0.0", 10001)));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(PortType.AUDIO_SOURCE, null, new Address(Family.AUTO, "0.0.0.0", 10001)));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(PortType.AUDIO_SOURCE, Protocol.RTP, null));
        }
    }

    @Test
    public void TestInvalidReadFloatArray() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            receiver.bind(PortType.AUDIO_SOURCE, Protocol.RTP, new Address(Family.AUTO, "0.0.0.0", 10001));
            receiver.bind(PortType.AUDIO_REPAIR, Protocol.RS8M_REPAIR, new Address(Family.AUTO, "0.0.0.0", 10002));
            assertThrows(IllegalArgumentException.class, () -> receiver.read(null));
        }
    }
}