package com.saveur221.enums;

public enum Statut {
    EN_ATTENTE,
    EN_PREPARATION,
    PRETE,
    RETIREE,
    ANNULEE;

    public boolean peutTransitionnerVers(Statut cible){
        if(this == RETIREE || this == ANNULEE){
            return false;
        }
        if(cible ==ANNULEE){
            return true; //annulation possible depuis n'importe quel etat non final
        }
        return switch(this){
            case EN_ATTENTE -> cible == EN_PREPARATION;
            case EN_PREPARATION -> cible == PRETE;
            case PRETE -> cible == RETIREE;
            default-> false;
        };
    }
}
