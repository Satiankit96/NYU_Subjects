import { Component, Input, OnInit, OnChanges, EventEmitter, Output } from '@angular/core';
import { ApplianceState } from 'src/app/models/ApplianceState';

@Component({
  selector: 'app-appliance',
  templateUrl: './appliance.component.html',
  styleUrls: ['./appliance.component.scss']
})
export class ApplianceComponent implements OnInit, OnChanges {

  @Input()
  inputApplianceState!: ApplianceState;

  @Output()
  outputApplianceState: EventEmitter<ApplianceState> = new EventEmitter();

  @Output()
  outputApplianceValue: EventEmitter<ApplianceState> = new EventEmitter();

  applianceCharacteristics = [
    {"appliance": "AC", "minValue" : 16, "maxValue" : 30, "step" : 1},
    {"appliance": "Light", "minValue" : 0, "maxValue" : 100, "step" : 5}
  ]

  applianceState: ApplianceState

  constructor() {
    this.applianceState = new ApplianceState();
  }

  ngOnInit(): void {
    this.applianceState = this.inputApplianceState;
  }

  ngOnChanges(): void {
      this.applianceState = this.inputApplianceState;
  }

  toggleAppliance(event: any): void {
    this.outputApplianceState.emit(event)
  }

  getApplianceCharacteristic(appliance: string, characteristic: string) {
    let element = this.applianceCharacteristics.find(element => element.appliance == appliance)

    switch(characteristic) {
      case 'min':
        return element?.minValue
      case 'max':
        return element?.maxValue
      case 'step':
        return element?.step
    }
    return
  }

  updateApplianceState(event: any, sourceId: string): void{
    event.source.id = sourceId
    this.outputApplianceValue.emit(event)
  }

}
