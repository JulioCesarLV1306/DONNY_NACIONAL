/*************************************
            VALIDAR LETRAS
*************************************/
let regExp = /[a-zA-Z]/g;
export function hasLetters(value:string){
    return regExp.test(value);
}

export function codificarRango(val1:number, val2:number):string{
    return `${val1}-${val2}`
}

export function decodificarRango(codificado: string):any[]{
    return codificado.split('-')
}





