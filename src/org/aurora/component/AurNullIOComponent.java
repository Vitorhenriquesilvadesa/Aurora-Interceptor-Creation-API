package org.aurora.component;

public class AurNullIOComponent extends AurIOComponent<AurNullIOComponent> {

    private static final AurNullIOComponent instance = new AurNullIOComponent();

    public AurNullIOComponent() {

    }

    @Override
    public AurIOComponent<AurNullIOComponent> clone() {
        return instance;
    }
}
