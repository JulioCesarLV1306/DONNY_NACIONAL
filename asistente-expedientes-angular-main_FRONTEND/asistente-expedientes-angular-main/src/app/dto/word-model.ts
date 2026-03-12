export class WordModel{
    constructor(command:string, value:any){
        this.command=command;
        this.value=value;
    }
    command!:string;
    value:any;
}