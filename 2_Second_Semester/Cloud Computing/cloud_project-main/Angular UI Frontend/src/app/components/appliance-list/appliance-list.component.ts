import { Component, OnInit, OnChanges } from '@angular/core';
import { ApplianceState, applianceStateMapper } from 'src/app/models/ApplianceState';
import { ConnectionHandlerService } from '../../services/connection-handler.service'

@Component({
  selector: 'app-appliance-list',
  templateUrl: './appliance-list.component.html',
  styleUrls: ['./appliance-list.component.scss']
})
export class ApplianceListComponent implements OnInit {

  applianceState: ApplianceState = new ApplianceState();

  lightState:ApplianceState = new ApplianceState(1, "Light", false, 0);
  acState: ApplianceState = new ApplianceState(2, "AC", false, 0);

  REFRESH_INTERVAL = 10000

  defaultState: ApplianceState[] = [this.acState, this.lightState]
  applianceStateList: ApplianceState[] = []

  constructor(private connectionHandler: ConnectionHandlerService) {}

  ngOnInit(): void {
    this.updateApplianceStateList()
    setInterval(() => this.updateApplianceStateList(), this.REFRESH_INTERVAL);
  }

  updateApplianceStateList():void {
    let applianceStateList: ApplianceState[] = []
    this.defaultState.map(appliance => {
      this.connectionHandler.getData(appliance).subscribe(result => {
        applianceStateList.push(applianceStateMapper(result))
      })
    })
    applianceStateList.sort((a,b) => a.id - b.id)
    this.applianceStateList = applianceStateList
  }

  updateApplianceState(event: any): void {
    const sourceId = event.source.id.split("-")[1];
    const state = event.checked;
    const appliance = this.applianceStateList.find(element => element.id == sourceId)

    if(appliance){
      appliance.state = state
      this.connectionHandler.putData(appliance).subscribe(res => console.log(res))
    }
  }

  updateApplianceValue(event: any): void {
    const sourceId = event.source.id;
    const value = event.value;
    const appliance = this.applianceStateList.find(element => element.appliance == sourceId)

    if(appliance){
      appliance.value = value
      this.connectionHandler.putData(appliance).subscribe(res => console.log(res))
    }
  }

}
