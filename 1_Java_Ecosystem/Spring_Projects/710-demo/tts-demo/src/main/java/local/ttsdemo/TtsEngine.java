package local.ttsdemo;

@FunctionalInterface
interface TtsEngine {
    Audio synthesize(TtsRequest request) throws Exception;
}
