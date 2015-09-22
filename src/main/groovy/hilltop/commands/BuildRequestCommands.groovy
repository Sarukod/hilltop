
package hilltop.commands

import hilltop.anthill.RequestFinder
import hilltop.anthill.ProjectFinder
import com.urbancode.anthill3.services.build.*
import com.urbancode.anthill3.domain.buildrequest.*
import com.urbancode.anthill3.domain.buildlife.*

class BuildRequestCommands extends AnthillCommands {
  def BuildRequestCommands(out) {
    super(out)
  }

  def open(id) {
    def request = work {
      finder(RequestFinder).one(id as int)
    }

    browse link_to(request)
  }

  def show(id) {
    send work {
      def request = finder(RequestFinder).one(id as int)

      map(request)
    }
  }

  def recent(projectName) {
    send work {
      def project = finder(ProjectFinder).one(projectName)
      def requests = finder(RequestFinder).recent(project)

      requests.collect { map(it) }
    }
  }

  def map(request) {
    def propertyNames = []
    if (request.propertyNames) {
      propertyNames = request.propertyNames
    }
    def buildLife = request.status == BuildRequestStatusEnum.BUILD_LIFE_CREATED ? request.buildLife : null
    def workflowCase = request.status == BuildRequestStatusEnum.STARTED_WORKFLOW ? request.workflowCase : null
    [
        id: request.id,
        name: "Build Request $request.id",
        url: link_to(request),
        project: request.project.name,
        workflow: request.workflow.name,
        requester: "$request.requesterName ($request.requestSource.name)",
        status: request.status.toString(),
        buildlife: buildLife ? buildLife.id.toString() : '',
        workflow_case: workflowCase ? workflowCase.id.toString() : '',
        workflow_case_status: workflowCase ? workflowCase.status.toString() : '',
        properties: propertyNames.collect {
          [key: it, value: request.getPropertyValue(it)]
        },
    ]
  }
}
