export class ApplianceState{
    id: number
    appliance: string
    state: boolean
    value: number

    constructor(id = 0, appliance = '', state = false, value = 0){
        this.id = id;
        this.appliance = appliance
        this.state = state;
        this.value  = value;
    }
}

export function applianceStateMapper(input: any){
  // @ts-ignore
  let as = input[0]
  // @ts-ignore
  return new ApplianceState(as['id'], as['name'], as['status'], as['value'])
}
